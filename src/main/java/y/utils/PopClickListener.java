package y.utils;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

public class PopClickListener extends MouseAdapter
{
	private JPopupMenu popupMenu;
	
	public PopClickListener(JPopupMenu popupMenu)
	{
		super();
		this.popupMenu = popupMenu;
	}
	
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger())
            doPop(e);
    }

    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger())
            doPop(e);
    }

    private void doPop(MouseEvent e){
    	popupMenu.show(e.getComponent(), e.getX(), e.getY());
    }
}
