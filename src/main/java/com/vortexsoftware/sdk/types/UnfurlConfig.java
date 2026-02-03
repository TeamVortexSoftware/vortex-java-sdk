package com.vortexsoftware.sdk.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Configuration for link unfurl (Open Graph) metadata.
 * Controls how the invitation link appears when shared on social platforms or messaging apps.
 *
 * <p>Example:</p>
 * <pre>{@code
 * UnfurlConfig unfurlConfig = new UnfurlConfig();
 * unfurlConfig.setTitle("Join our team!");
 * unfurlConfig.setDescription("You've been invited to collaborate");
 * unfurlConfig.setImage("https://example.com/og-image.png");
 * unfurlConfig.setType("website");
 * unfurlConfig.setSiteName("Acme App");
 * }</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UnfurlConfig {

    /** The title shown in link previews (og:title) */
    @JsonProperty("title")
    private String title;

    /** The description shown in link previews (og:description) */
    @JsonProperty("description")
    private String description;

    /** The image URL shown in link previews (og:image) - must be HTTPS */
    @JsonProperty("image")
    private String image;

    /** The Open Graph type (og:type) - e.g., 'website', 'article', 'product' */
    @JsonProperty("type")
    private String type;

    /** The site name shown in link previews (og:site_name) */
    @JsonProperty("siteName")
    private String siteName;

    public UnfurlConfig() {
    }

    public UnfurlConfig(String title, String description, String image, String type, String siteName) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.type = type;
        this.siteName = siteName;
    }

    // Getters and Setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }
}
