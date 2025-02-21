package store.anygood.model.dto;

public class TelephoneInfoDTO {

    private String telephoneId;
    private String country;

    private String language;

    public TelephoneInfoDTO() {
    }

    public TelephoneInfoDTO(String telephoneId, String country, String language) {
        this.telephoneId = telephoneId;
        this.country = country;
        this.language = language;
    }

    public String getTelephoneId() {
        return telephoneId;
    }

    public void setTelephoneId(String telephoneId) {
        this.telephoneId = telephoneId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
