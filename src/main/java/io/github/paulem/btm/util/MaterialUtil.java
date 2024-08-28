package io.github.paulem.btm.util;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public class MaterialUtil {
	
	// Method to find the most common ingredient
    public static Material commonIngredient(ItemStack item) {
        Map<Material, Integer> ingredientCount = new HashMap<>();

        // Iterate through all recipes for the given item
        for (Recipe recipe : Bukkit.getServer().getRecipesFor(new ItemStack(item.getType()))) {
            if (recipe instanceof ShapedRecipe) {
                ShapedRecipe shaped = (ShapedRecipe) recipe;
                for (ItemStack ingredient : shaped.getIngredientMap().values()) {
                    if (ingredient != null) {
                        Material type = ingredient.getType();
                        ingredientCount.put(type, ingredientCount.getOrDefault(type, 0) + 1);
                    }
                }
            } else if (recipe instanceof ShapelessRecipe) {
                ShapelessRecipe shapeless = (ShapelessRecipe) recipe;
                for (ItemStack ingredient : shapeless.getIngredientList()) {
                    if (ingredient != null) {
                        Material type = ingredient.getType();
                        ingredientCount.put(type, ingredientCount.getOrDefault(type, 0) + 1);
                    }
                }
            }
        }

        // Find the most common material
        Material mostCommon = null;
        int maxCount = 0;
        for (Map.Entry<Material, Integer> entry : ingredientCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostCommon = entry.getKey();
            }
        }
	Bukkit.getLogger().info(item.getType().name()+" ingredient:"+mostCommon.name());
        return mostCommon;
    }

    // Method to check if the player has the material
    public static boolean hasItem(Player player, ItemStack item) {
    	return player.getInventory().containsAtLeast(item, 1);
    }
}
