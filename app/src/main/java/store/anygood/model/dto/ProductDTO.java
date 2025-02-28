package store.anygood.model.dto;

public class ProductDTO {
    private String name;
    private String keyword; // This keyword will be used to generate the Amazon link
    private String link;
    private String description;

    public ProductDTO(String name, String keyword, String link, String description) {
        this.name = name;
        this.keyword = keyword;
        this.link = link;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }
}
