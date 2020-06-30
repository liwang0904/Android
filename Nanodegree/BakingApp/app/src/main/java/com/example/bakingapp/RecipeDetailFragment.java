package com.example.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.parceler.Parcels;

import java.util.Objects;
import java.util.Set;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm;

public class RecipeDetailFragment extends Fragment implements VerticalStepperForm {
    private OnFragmentInteractionListener listener;
    private Recipe recipe;
    private Boolean isTwoPane;
    private String[] steps;
    private VerticalStepperFormLayout verticalStepperForm;

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(int i);
        void onChange(Integer id);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        Bundle bundle;
        if (getArguments() == null)
            verticalStepperForm.setVisibility(View.GONE);
        else {
            bundle = getArguments();
            recipe = Parcels.unwrap(bundle.getParcelable(Objects.requireNonNull(getContext()).getPackageName()));
            if (bundle.containsKey("isTwoPane"))
                isTwoPane = bundle.getBoolean("isTwoPane");
            TextView textView = view.findViewById(R.id.heading_tv);
            textView.setText(recipe.getName());
            setUpIngredients(view);
            handleStepper(view);
            setUpBottomSheet(view);
            handleFragment(view);
            CoordinatorLayout coordinatorLayout = view.findViewById(R.id.stepper_parent_layout_cl);
            if (!getResources().getBoolean(R.bool.isTablet))
                coordinatorLayout.setBackgroundColor(getResources().getColor(R.color.white));
            else
                coordinatorLayout.setBackgroundColor(getResources().getColor(R.color.transparent));
        }
        return view;
    }

    private void setUpIngredients(View view) {
        TextView ingredients = view.findViewById(R.id.ingredients_tv);
        StringBuilder builder = new StringBuilder();
        for (Ingredient ingredient: recipe.getIngredients()) {
            builder.append(ingredient.getQuantity())
                    .append(" ")
                    .append(ingredient.getMeasure())
                    .append(" of ")
                    .append(ingredient.getIngredient())
                    .append("\n");
        }
        ingredients.setText(builder);
    }

    private void handleStepper(View view) {
        steps = new String[recipe.getSteps().size()];
        int i = 0;
        for (int j = steps.length - 1; j <= 0; j--) {
            steps[i] = recipe.getSteps().get(j).getShortDescription();
            i++;
        }
        verticalStepperForm = view.findViewById(R.id.vertical_stepper_form);
        VerticalStepperFormLayout.Builder.newInstance(verticalStepperForm, steps, this, getActivity())
                .primaryColor(ContextCompat.getColor(getContext(), R.color.black))
                .primaryDarkColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.white))
                .displayBottomNavigation(false)
                .init();
        for (int k = 0; k < steps.length; k++) {
            verticalStepperForm.setStepTitle(k, recipe.getSteps().get(k).getShortDescription());
        }
    }

    private void handleFragment(final View view) {
        FloatingActionButton changeButton = view.findViewById(R.id.change_btn);
        Button peekLayout = view.findViewById(R.id.peek_layout);
        peekLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                RecipeWidget.selectRecipe(recipe.getId(), RecipeDetailFragment.this.getContext());
                Toast.makeText(view.getContext(), "Recipe saved to widget!", Toast.LENGTH_SHORT).show();
            }
        });

        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {
                int id = recipe.getId();
                Set<String> set = RecipeRepository.getInstance().getRecipeSet(Objects.requireNonNull(RecipeDetailFragment.this.getContext()));
                if (id < set.size())
                    id += 1;
                else
                    id = 1;
                listener.onChange(id);
            }
        });
    }

    private void setUpBottomSheet(View view) {
        LinearLayout bottomSheet = view.findViewById(R.id.bottom_layout);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                switch (i) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                    case BottomSheetBehavior.STATE_SETTLING:
                    case BottomSheetBehavior.STATE_DRAGGING:
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                    }
                    break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Parcelable parcelable = verticalStepperForm.onSaveInstanceState();
        outState.putParcelable("verticalStepForm", parcelable);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey("verticalStepForm"))
            verticalStepperForm.onRestoreInstanceState(savedInstanceState.getParcelable("verticalStepForm"));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (OnFragmentInteractionListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public View createStepContentView(final int step) {
        View view = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.stepper_layout, null, false);
        TextView shortDescription = view.findViewById(R.id.stepper_description);
        shortDescription.setText("Click here for more information");
        if (!recipe.getSteps().get(step).getThumbnailURL().equals("")) {
            ImageView recipeImage = view.findViewById(R.id.recipe_image_iv);
            Glide.with(getContext()).load(recipe.getSteps().get(step).getThumbnailURL()).into(recipeImage);
        }
        if (!isTwoPane) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view1) {
                    Intent intent = new Intent(RecipeDetailFragment.this.getContext(), StepDetailActivity.class);
                    intent.putExtra(RecipeDetailFragment.this.getContext().getPackageName(), Parcels.wrap(recipe));
                    intent.putExtra("position", step);
                    RecipeDetailFragment.this.startActivity(intent);
                }
            });
        } else {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view1) {
                    listener.onFragmentInteraction(step);
                }
            });
        }
        return view;
    }

    @Override
    public void onStepOpening(int step) {
        if (isTwoPane && step < steps.length)
            listener.onFragmentInteraction(step);
        verticalStepperForm.setActiveStepAsCompleted();
    }

    @Override
    public void sendData() {
        getActivity().finish();
    }
}