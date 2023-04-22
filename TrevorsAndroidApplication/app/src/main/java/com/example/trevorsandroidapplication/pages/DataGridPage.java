package com.example.trevorsandroidapplication.pages;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trevorsandroidapplication.R;
import com.example.trevorsandroidapplication.WeightAppMain;
import com.example.trevorsandroidapplication.objects.User;
import com.example.trevorsandroidapplication.objects.Weight;
import com.example.trevorsandroidapplication.util.AppUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SuppressLint("NotifyDataSetChanged")
public class DataGridPage extends AppPage {
    private final List<Weight> weightItems = new ArrayList<>();
    private final User user;
    private final TextView weightGoalView;
    private final EditText weightGoalText;
    private final EditText weightAmountText;
    private final Button editGoalButton;
    private final Button addItemButton;
    private final ItemAdaptor itemAdaptor;

    public DataGridPage(WeightAppMain main, User user) {
        super(main);
        this.user = user;
        this.weightGoalView = main.findViewById(R.id.weightLogText);
        this.weightGoalText = main.findViewById(R.id.goalAmount);
        this.weightAmountText = main.findViewById(R.id.weightAmount);
        this.editGoalButton = main.findViewById(R.id.updateWeightGoal);
        this.addItemButton = main.findViewById(R.id.addWeightButton);

        ItemAdaptor itemAdaptor = new ItemAdaptor(weightItems, main);
        RecyclerView recyclerView = main.findViewById(R.id.lvItems);
        recyclerView.setAdapter(itemAdaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(main));
        this.itemAdaptor = itemAdaptor;
    }

    @Override
    public void init() {
        updateWeightGoalTextView(user.getWeightGoal());

        loadWeightsFromDatabase();

        registerWeightGoalEditing();

        registerWeightAmountEditing();
    }

    private void loadWeightsFromDatabase() {
        main.getWeightDatabase().getAllWeights(user).thenAccept(weights -> {
            AppUtil.runSync(() -> {
                weightItems.addAll(weights);
                itemAdaptor.notifyDataSetChanged();
            });
        });
    }

    private void registerWeightGoalEditing() {
        editGoalButton.setOnClickListener(view -> {
            handleGetIntegerFromTextBox(weightGoalText, view).ifPresent(amount -> {
                updateWeightGoalTextView(amount);

                // Updating weight
                user.setWeightGoal(amount);
                main.getUserDatabase().updateUserWeight(user);

                // Checking if the weight goal was reached
                weightItems.forEach(weight -> {
                    handleIfWeightGoalIsMet(weight.getWeight());
                });
            });
        });
    }

    private void registerWeightAmountEditing() {
        addItemButton.setOnClickListener(view -> {
            if (!user.hasUserSetWeightGoal()) {
                weightAmountText.setError("Must set weight goal first!");
                return;
            }

            handleGetIntegerFromTextBox(weightAmountText, view).ifPresent(amount -> {
                Weight weight = new Weight(UUID.randomUUID(), amount, new Date().toString());
                weightItems.add(weight);
                itemAdaptor.notifyDataSetChanged();

                // Adding to database
                main.getWeightDatabase().addWeight(user, weight);

                // Handling if goal is met
                handleIfWeightGoalIsMet(amount);
            });
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateWeightGoalTextView(int amount) {
        weightGoalView.setText("Weight Goal " + amount);
    }


    /**
     * This is it's own function as this is sort of a tricky
     * method to do, as the check really should be based on
     * if the user is trying to gain or lose weight
     *
     * For simplicity it is assumed the user is trying to
     * lose weight with this check
     *
     * Also handling SMS messaging here
     */
    private void handleIfWeightGoalIsMet(int weightToCheck) {
        // Goal is met
        if (weightToCheck <= user.getWeightGoal()) {
            AppUtil.attemptToSendSMSMessage(main, String.format("Your weight goal of %s was reached! POG CHAMP!!!",
                    user.getWeightGoal()));
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private Optional<Integer> handleGetIntegerFromTextBox(EditText editText, View view) {
        String textAmount = editText.getText().toString();

        try {
            int amount = Integer.parseInt(textAmount);

            // Clearing text field
            editText.setText("");

            // Dismissing keyboard
            InputMethodManager imm = (InputMethodManager) main.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            return Optional.of(amount);
        } catch (Exception e) {
            editText.setError("Must input a valid weight!");
            return Optional.empty();
        }
    }

    private static class ItemAdaptor extends RecyclerView.Adapter<CustomViewHolder> {
        private final WeightAppMain main;
        private final List<Weight> weightItems;

        private ItemAdaptor(List<Weight> weightItems, WeightAppMain main) {
            this.weightItems = weightItems;
            this.main = main;
        }

        @NonNull
        @Override
        public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.weight_list_item, parent, false);

            return new CustomViewHolder(view).linkAdapter(this);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
            holder.getEditText().setText(weightItems.get(position).getWeight() + "");
        }

        @Override
        public int getItemCount() {
            return weightItems.size();
        }
    }

    private static class CustomViewHolder extends RecyclerView.ViewHolder {
        private final EditText editText;
        private ItemAdaptor itemAdaptor;

        public CustomViewHolder(View itemView) {
            super(itemView);
            this.editText = itemView.findViewById(R.id.weightItem);

            itemView.findViewById(R.id.weightItemRemove).setOnClickListener(view -> {
                int position = getAdapterPosition();

                // Sometimes it's -1 for some reason (which breaks things)
                if (position < 0) return;

                itemAdaptor.main.getWeightDatabase().removeWeight(itemAdaptor.weightItems.get(position));
                itemAdaptor.weightItems.remove(position);
                itemAdaptor.notifyItemRemoved(position);
            });
        }

        public CustomViewHolder linkAdapter(ItemAdaptor itemAdaptor) {
            this.itemAdaptor = itemAdaptor;
            return this;
        }

        public EditText getEditText() {
            return editText;
        }
    }
}
