package com.javierruiz.shutterstocktestapp.model.json;

import java.io.Serializable;

public class ImageInfo implements Serializable {

    public String id;
    public Double aspect;
    public ImageAssets assets;
    public ImageContributor contributor;
    public String description;
    public String imageType;
    public String mediaType;

}
