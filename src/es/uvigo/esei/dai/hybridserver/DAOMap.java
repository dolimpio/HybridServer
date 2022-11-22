package es.uvigo.esei.dai.hybridserver;

import java.util.Map;
import java.util.Set;

public class DAOMap implements DAO {
    Map<String, String> pages;

    public DAOMap(Map<String, String> pages) {
        this.pages = pages;
    }

    @Override
    public void create(String uuid, String content) {
        pages.put(uuid, content);
    }

    @Override
    public void update(String uuid, String content) {
        pages.put(uuid, content);
    }

    @Override
    public void delete(String uuid) {
        pages.remove(uuid);
    }

    @Override
    public String get(String uuid) {
        return pages.get(uuid);
    }

    @Override
    public Set<String> list() {
        Set<String> list = pages.keySet();
        return list;
    }

    @Override
    public boolean exists(String uuid) {
        return pages.containsKey(uuid);
    }

}
