package shutterstock.test.com.shutterstockapp.model.json;

import java.util.ArrayList;
import java.util.List;

public class SearchResponse {

    public Integer page;
    public Integer perPage;
    public Integer totalCount;
    public String searchId;
    public List<Datum> data = new ArrayList<Datum>();

}
