package com.fashionhub.util;

public class ProductImageUtil {

    // Reliable Unsplash Fashion Images (Production-safe)
    // Local Static Image Paths (Place in src/main/resources/static/images/)
    private static final String IMG_SHIRT = "/images/shirt.jpg";
    private static final String IMG_JEANS = "/images/jeans.jpg";
    private static final String IMG_TOPS = "/images/tops.jpg";
    private static final String IMG_KURTHA = "/images/kurtha.jpg";
    private static final String IMG_DEFAULT = "/images/default.jpg";

    // Public fallback for UI
    public static final String FALLBACK_IMAGE = IMG_DEFAULT;

    /**
     * Determines a suitable fashion image URL based on the product's category name
     * or product name.
     * 
     * @param categoryName Name of the product's category
     * @param productName  Name of the product
     * @return A themed image URL from local resources
     */
    public static String getImageUrl(String categoryName, String productName) {
        String input = (categoryName + " " + productName).toLowerCase();

        if (input.contains("shirt") || input.contains("t-shirt") || input.contains("tee")) {
            return IMG_SHIRT;
        } else if (input.contains("pant") || input.contains("jean") || input.contains("trousers")) {
            return IMG_JEANS;
        } else if (input.contains("top")) {
            return IMG_TOPS;
        } else if (input.contains("kurtha") || input.contains("kurti") || input.contains("ethnic") || input.contains("sari")) {
            return IMG_KURTHA;
        }

        return IMG_DEFAULT;
    }
}
