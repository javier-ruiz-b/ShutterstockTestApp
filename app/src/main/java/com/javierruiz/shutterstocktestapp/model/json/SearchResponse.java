package com.javierruiz.shutterstocktestapp.model.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SearchResponse implements Serializable {

    public Integer page;
    public Integer perPage;
    public Integer totalCount;
    public String searchId;
    public List<ImageInfo> data = new ArrayList<ImageInfo>();

}
