package com.archi.intrisfeed.custome_control;

/**
 * Created by archi_info on 9/27/2016.
 */
public interface DrawableClickListener {
    public static enum DrawablePosition { TOP, BOTTOM, LEFT, RIGHT };
    public void onClick(DrawablePosition target);
}
