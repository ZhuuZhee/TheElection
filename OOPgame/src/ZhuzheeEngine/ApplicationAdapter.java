package ZhuzheeEngine;

public interface ApplicationAdapter {
    public void create();
    public void resize(int width, int height);
    public void render();
    public void dispose();
}
