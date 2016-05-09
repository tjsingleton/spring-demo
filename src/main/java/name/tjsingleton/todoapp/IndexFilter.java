package name.tjsingleton.todoapp;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IndexFilter {

    List<UUID> ids = new ArrayList<>();

    public List<UUID> getIds() {
        return ids;
    }

    public void setIds(List<UUID> ids) {
        this.ids = ids;
    }
}
