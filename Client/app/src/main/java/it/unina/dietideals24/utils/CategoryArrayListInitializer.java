package it.unina.dietideals24.utils;

import android.app.Activity;
import android.content.Context;

import java.util.ArrayList;

import it.unina.dietideals24.adapter.entity.CategoryItem;
import it.unina.dietideals24.enumerations.CategoryEnum;

public class CategoryArrayListInitializer {
    private CategoryArrayListInitializer() {
    }

    /**
     * This method takes all the constants in the CategoryEnum and initializes an array of strings
     *
     * @return The array of category names
     */
    public static ArrayList<String> getAllCategoryNames() {
        ArrayList<String> categories = new ArrayList<>();

        for (CategoryEnum category : CategoryEnum.values()) {
            categories.add(capitalize(category.toString()));
        }

        return categories;
    }

    /**
     * This method takes the first six the constants in the CategoryEnum and initializes an array of CategoryItem
     * with category name and resource id (icon)
     *
     * @param context  used to access resources
     * @param activity reference to activity
     * @return The array of CategoryItem
     */
    public static ArrayList<CategoryItem> getFirstSixCategoryItems(Context context, Activity activity) {
        ArrayList<CategoryItem> categories = new ArrayList<>();

        CategoryEnum[] values = CategoryEnum.values();
        for (int i = 0; i < values.length && i < 6; i++) {
            CategoryEnum category = values[i];
            categories.add(new CategoryItem(capitalize(category.toString()), iconCategory(category.toString(), context, activity)));
        }

        return categories;
    }

    /**
     * This method takes all the constants in the CategoryEnum and initializes an array of CategoryItem
     * with category name and resource id (icon)
     *
     * @param context  used to access resources
     * @param activity reference to activity
     * @return The array of CategoryItem
     */
    public static ArrayList<CategoryItem> getAllCategoryItems(Context context, Activity activity) {
        ArrayList<CategoryItem> categories = new ArrayList<>();

        for (CategoryEnum category : CategoryEnum.values()) {
            categories.add(new CategoryItem(capitalize(category.toString()), iconCategory(category.toString(), context, activity)));
        }

        return categories;
    }

    /**
     * This method converts the first character of a given string to uppercase; while the remainder is lowercase
     *
     * @param str string to capitalise
     */
    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    /**
     * this method automatically sets the icon string of a category, based on the input parameter, and returns it
     *
     * @param category category name
     */
    public static int iconCategory(String category, Context context, Activity activity) {
        String iconName = "round_" + category.toLowerCase() + "_24";
        return context.getResources().getIdentifier(iconName, "drawable", activity.getPackageName());
    }
}
