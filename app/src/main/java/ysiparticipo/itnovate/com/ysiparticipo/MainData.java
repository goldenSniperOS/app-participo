package ysiparticipo.itnovate.com.ysiparticipo;

/**
 * Created by Angel Sirlopu C on 13/12/2015.
 */
public class MainData{
    private String title;
    private int resource;

    public MainData(String _title, int _resource){
        title  = _title;
        resource = _resource;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }
}
