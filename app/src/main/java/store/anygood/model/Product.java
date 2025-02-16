package store.anygood.model;

public class Product {
    private String name;
    private String price;
    private String keyword; // This keyword will be used to generate the Amazon link

    public Product(String name, String price, String keyword) {
        this.name = name;
        this.price = price;
        this.keyword = keyword;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getKeyword() {
        return keyword;
    }
}
