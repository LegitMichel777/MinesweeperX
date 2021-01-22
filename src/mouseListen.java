import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class mouseListen extends MouseAdapter {
    public static List<JLabel> isActive= new ArrayList<JLabel>();
    @Override
    public void mousePressed(MouseEvent e) {
        MinesweeperX.mouseDown = true;
        JLabel clicked = (JLabel) e.getSource();
        String clcnm = clicked.getName();
        if (clcnm == "sweep") {
            MinesweeperX.updt();
        } else if (clcnm == "pref") {
            if (glovar.optionsActive == false) {
                new Options();
                glovar.optionsActive = true;
            }
        } else if (MinesweeperX.keepGoing&&!MinesweeperX.connectedToBot) {
            int cx, cy;
            cx = Integer.parseInt(clcnm.substring(0, clcnm.indexOf(' ')));
            cy = Integer.parseInt(clcnm.substring(clcnm.indexOf(' ') + 1, clcnm.length()));
            glovar.coord dis = new glovar.coord();
            dis.x = cx;
            dis.y = cy;
            if (glovar.virgin) {
                glovar.uncovr.push(dis);
            } else {
                if (MinesweeperX.covered[cx][cy] == 1) {
                    if (glovar.shiftDown) {
                        clicked.setIcon(MinesweeperX.mnpic[20]);
                        MinesweeperX.covered[cx][cy] = -1;
                        MinesweeperX.minesLeft--;
                    } else glovar.uncovr.push(dis);
                } else if (MinesweeperX.covered[cx][cy] == 0) {
                    if (glovar.chord) {
                        glovar.chorded.push(dis);
                    }
                } else if (MinesweeperX.covered[cx][cy] == -1) {
                    clicked.setIcon(MinesweeperX.mnpic[18]);
                    MinesweeperX.covered[cx][cy] = 1;
                    MinesweeperX.minesLeft++;
                }
            }
        }
    }
    public void mouseClicked(MouseEvent e) { }
    public void mouseReleased(MouseEvent e) {
        MinesweeperX.mouseDown=false;
    }
    public void mouseEntered(MouseEvent e) {
        if (MinesweeperX.keepGoing) {
            int cx, cy;
            JLabel clicked = (JLabel) e.getSource();
            String clcnm = clicked.getName();
            if (clcnm!="sweep"&&clcnm!="pref") {
                cx = Integer.parseInt(clcnm.substring(0, clcnm.indexOf(' ')));
                cy = Integer.parseInt(clcnm.substring(clcnm.indexOf(' ') + 1, clcnm.length()));
                if (MinesweeperX.frame.hasFocus()) {
                    if (MinesweeperX.mnnum[cx][cy] != -1 && MinesweeperX.covered[cx][cy] == 0) {
                        clicked.setIcon(MinesweeperX.mnpic[MinesweeperX.mnnum[cx][cy] + 9]);
                        isActive.add(clicked);
                    } else if (MinesweeperX.covered[cx][cy] == 1) {
                        clicked.setIcon(MinesweeperX.mnpic[19]);
                        isActive.add(clicked);
                    }
                }
            }
        }
    }
    public void mouseExited(MouseEvent e) {
        if (MinesweeperX.keepGoing) {
            if (isActive.size() > 1) {
                Iterator<JLabel> it = isActive.iterator();
                while (it.hasNext()) {
                    JLabel clicked = it.next();
                    String clcnm = clicked.getName();
                    int cx, cy;
                    cx = Integer.parseInt(clcnm.substring(0, clcnm.indexOf(' ')));
                    cy = Integer.parseInt(clcnm.substring(clcnm.indexOf(' ') + 1, clcnm.length()));
                    if (MinesweeperX.mnnum[cx][cy] != -1 && MinesweeperX.covered[cx][cy] == 0) {
                        clicked.setIcon(MinesweeperX.mnpic[MinesweeperX.mnnum[cx][cy]]);
                    } else if (MinesweeperX.covered[cx][cy] == 1) {
                        clicked.setIcon(MinesweeperX.mnpic[18]);
                    }
                }
                isActive.clear();
            }
            JLabel clicked = (JLabel) e.getSource();
            String clcnm = clicked.getName();
            if (clcnm!="sweep"&&clcnm!="pref") {
                int cx, cy;
                cx = Integer.parseInt(clcnm.substring(0, clcnm.indexOf(' ')));
                cy = Integer.parseInt(clcnm.substring(clcnm.indexOf(' ') + 1, clcnm.length()));
                if (MinesweeperX.frame.hasFocus()) {
                    if (MinesweeperX.covered[cx][cy] == 0 && MinesweeperX.mnnum[cx][cy] != -1) {
                        clicked.setIcon(MinesweeperX.mnpic[MinesweeperX.mnnum[cx][cy]]);
                    } else if (MinesweeperX.covered[cx][cy] == 1) {
                        clicked.setIcon(MinesweeperX.mnpic[18]);
                    }
                }
            }
        } else {
            Iterator<JLabel> it = isActive.iterator();
            while (it.hasNext()) {
                JLabel clicked = it.next();
                String clcnm = clicked.getName();
                int cx, cy;
                cx = Integer.parseInt(clcnm.substring(0, clcnm.indexOf(' ')));
                cy = Integer.parseInt(clcnm.substring(clcnm.indexOf(' ') + 1, clcnm.length()));
                if (MinesweeperX.mnnum[cx][cy] != -1 && MinesweeperX.covered[cx][cy] == 0) {
                    clicked.setIcon(MinesweeperX.mnpic[MinesweeperX.mnnum[cx][cy]]);
                } else if (MinesweeperX.covered[cx][cy] == 1) {
                    clicked.setIcon(MinesweeperX.mnpic[18]);
                }
            }
            isActive.clear();
        }
    }
    public void mouseDragged(MouseEvent e){}
    public void mouseMoved(MouseEvent e){}
}
