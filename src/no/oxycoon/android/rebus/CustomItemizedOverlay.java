package no.oxycoon.android.rebus;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class CustomItemizedOverlay extends ItemizedOverlay
{

    private ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();
    
    public CustomItemizedOverlay(Drawable defaultMarker)
    {
        super(boundCenterBottom(defaultMarker));
    }

    @Override
    protected OverlayItem createItem(int i)
    {
        return overlays.get(i);
    }

    @Override
    public int size()
    {
        return overlays.size();
    }
    
    public void addOverlay(OverlayItem overlay)
    {
        overlays.add(overlay);
        populate();
    }
}
