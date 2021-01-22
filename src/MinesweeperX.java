import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static java.lang.Thread.sleep;
import static java.util.Collections.shuffle;

public class MinesweeperX implements KeyListener {
    public static ImageIcon[] mnpic=new ImageIcon[29]; //0-8:numbers. //9-17:Hover numbers 18:covered 19:flag 20:ded 21:smiley 22:shock 23:ded
    public static ImageIcon[] numcon=new ImageIcon[11];
    public static ArrayList<ArrayList<JLabel>>grid=new ArrayList<ArrayList<JLabel>>();
    public static JFrame frame = new JFrame();
    JLabel smiley,pref,timecon[],minel[];
    public static boolean keepGoing,rcvd,connectedToBot;
    public static int mnnum[][] = null;
    public static int covered[][]=null; //-1:Flag 0:Not covered 1:Covered
    public static boolean nextGame=false;
    public static boolean mouseDown=false;
    int[][] dir={{1,-1},{1,0},{1,1},{0,1},{0,-1},{-1,-1},{-1,0},{-1,1}};
    boolean masterVirgin=true;
    public static int minesLeft;
    public static boolean timerActive=false;
    Instant timerStart;
    public MinesweeperX() {
        glovar.uncovr.clear();
        glovar.chorded.clear();
        mouseListen mslst= new mouseListen();
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addKeyListener(this);
        frame.setResizable(false);
        frame.setLocation(10, 10);
        frame.getContentPane().setBackground(new Color(53, 53, 53));
        boolean needsBak=false;
        while (true) {
            while (!nextGame) {
                try {
                    sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            int lstTime=-1,lstMine=-1;
            frame.setVisible(false);
            mouseListen.isActive.clear();
            for (int i=0;i<grid.size();i++) {
                for (int j=0;j<grid.get(i).size();j++) {
                    frame.remove(grid.get(i).get(j));
                }
            }
            if (!masterVirgin) {
                frame.remove(smiley);
                frame.remove(pref);
                for (int i=0;i<4;i++) frame.remove(timecon[i]);
                for (int i=0;i<3;i++) frame.remove(minel[i]);
            }
            masterVirgin=false;
            mnnum=new int[glovar.szx][glovar.szy];
            covered=new int[glovar.szx][glovar.szy];
            nextGame=false;
            int elmset=0;
            if (glovar.szx<12) {
                elmset=60;
            }
            int windowoffset = 22;
            frame.setSize(32 * glovar.szx, 32 * glovar.szy + windowoffset + 70+elmset);
            frame.setResizable(false);
            smiley = new JLabel(mnpic[22]);
            smiley.setName("sweep");
            smiley.addMouseListener(mslst);
            smiley.setBounds(frame.getWidth() / 2 - 24, 11+elmset, 48, 48);
            pref=new JLabel(mnpic[28]);
            pref.setName("pref");
            pref.addMouseListener(mslst);
            pref.setBounds(frame.getWidth()-48-20,11+elmset,48,48);
            timecon=new JLabel[4];
            for (int i=0;i<4;i++) {
                timecon[i]=new JLabel(numcon[0]);
                timecon[i].setName("timek"+i);
                if (elmset==0) timecon[i].setBounds(frame.getWidth() / 2 - 24-(4-i)*26-15,11,26,48);
                else timecon[i].setBounds(i*26+15,11,26,48);
                frame.add(timecon[i]);
            }
            minel=new JLabel[3];
            for (int i=0;i<3;i++) {
                minel[i]=new JLabel(numcon[0]);
                minel[i].setName("minel"+i);
                if (elmset==0) minel[i].setBounds(frame.getWidth() / 2 + 24+i*26+15,11,26,48);
                else minel[i].setBounds(frame.getWidth()-15-(3-i)*26,11,26,48);
                frame.add(minel[i]);
            }
            if (elmset==0) frame.setTitle("MinesweeperX Pro Max");
            else frame.setTitle("MinesweeperX Pro");
            minesLeft=glovar.minecnt;
            int mineslc=minesLeft;
            long[] minedi =new long[3];
            int cum=0;
            while (mineslc!=0) {
                minedi[cum]=mineslc%10;
                cum++;
                mineslc/=10;
            }
            for (int i=0;i<3;i++) {
                if (minedi[2-i]==-1) minel[i].setIcon(numcon[10]);
                else minel[i].setIcon(numcon[(int) minedi[2-i]]);
            }
            frame.add(pref);
            frame.add(smiley);
            grid.clear();
            for (int i = 0; i < glovar.szx; i++) {
                ArrayList<JLabel> dis = new ArrayList<>();
                for (int j = 0; j < glovar.szy; j++) {
                    covered[i][j] = 1;
                    JLabel toIns = new JLabel(mnpic[18]);
                    toIns.setName(i + " " + j);
                    toIns.addMouseListener(mslst);
                    toIns.setBounds(i * 32, j * 32 + 70+elmset, 32, 32);
                    dis.add(toIns);
                }
                grid.add(dis);
            }

            for (int i = 0; i < glovar.szx; i++) {
                for (int j = 0; j < glovar.szy; j++) {
                    frame.add(grid.get(i).get(j));
                }
            }
            frame.setVisible(true);
            rcvd = false;
            glovar.virgin = true;
            int blocksLeft = glovar.szx * glovar.szy - glovar.minecnt;
            keepGoing = true;
            timerActive=false;
            while (keepGoing) {
                if (needsBak) {
                    if (!mouseDown) {
                        smiley.setIcon(mnpic[22]);
                        needsBak=false;
                        frame.repaint(frame.getWidth() / 2 - 24, 11+elmset, 48, 48);
                    }
                }
                if (timerActive) {
                    //update icon
                    long sec2= Duration.between(timerStart,Instant.now()).toSeconds();
                    if (sec2!=lstTime) {
                        lstTime= (int) sec2;
                        if (sec2 > 0) {
                            if (sec2 > 9999) {
                                for (int i = 0; i < 4; i++) {
                                    timecon[i].setIcon(numcon[9]);
                                }
                            } else {
                                long[] timedi = new long[4];
                                cum = 0;
                                while (sec2 != 0) {
                                    timedi[cum] = sec2 % 10;
                                    cum++;
                                    sec2 /= 10;
                                }
                                for (int i = 0; i < 4; i++) {
                                    timecon[i].setIcon(numcon[(int) timedi[3 - i]]);
                                }
                            }
                        }
                        if (elmset == 0) {
                            frame.repaint(frame.getWidth() / 2 - 24 - 4 * 26 - 15, 11, 104, 48);
                        } else {
                            frame.repaint(15, 11, 104, 48);
                        }
                    }
                    mineslc=minesLeft;
                    if (mineslc!=lstMine) {
                        lstMine=mineslc;
                        minedi = new long[3];
                        cum = 0;
                        boolean minelneg = false;
                        if (mineslc < 0) {
                            minedi[2] = -1;
                            mineslc = -mineslc;
                            minelneg = true;
                        }
                        if (!minelneg || (minelneg && mineslc <= 99)) {
                            while (mineslc != 0) {
                                minedi[cum] = mineslc % 10;
                                cum++;
                                mineslc /= 10;
                            }
                        } else {
                            minedi[0] = minedi[1] = 9;
                        }
                        for (int i = 0; i < 3; i++) {
                            if (minedi[2 - i] == -1) minel[i].setIcon(numcon[10]);
                            else minel[i].setIcon(numcon[(int) minedi[2 - i]]);
                        }
                        if (elmset==0) {
                            frame.repaint(frame.getWidth() / 2 + 24+15,11,78,48);
                        } else {
                            frame.repaint(frame.getWidth()-15-3*26,11,78,48);
                        }
                    }
                }
                if (connectedToBot&&!glovar.optionsActive) {
                    BufferedWriter comMap;
                    try {
                        comMap = new BufferedWriter(new FileWriter("/Users/legitmichel777/Desktop/sweep/botComBig.txt"));
                        comMap.write("MINESLEFT\n");
                        comMap.write(minesLeft+"\n");
                        comMap.write("MAPSIZEX\n");
                        comMap.write(glovar.szy+"\n");
                        comMap.write("MAPSIZEY\n");
                        comMap.write(glovar.szx+"\n");
                        comMap.write("MAP\n");
                        if (glovar.virgin) comMap.write("EMPTY");
                        else {
                            for (int i = 0; i < glovar.szy; i++) {
                                for (int j = 0; j < glovar.szx; j++) {
                                    if (covered[j][i]==1) {
                                        comMap.write("-1");
                                    } else {
                                        comMap.write(String.valueOf(mnnum[j][i]));
                                    }
                                    comMap.write(" ");
                                }
                                comMap.write('\n');
                            }
                        }
                        comMap.close();
                        comMap = new BufferedWriter(new FileWriter("/Users/legitmichel777/Desktop/sweep/botCom.txt"));
                        comMap.write("MAP");
                        comMap.close();
                    } catch (IOException ex) {
                        System.out.println("Expected bot directory missing!");
                    }
                }
                readBot:while (connectedToBot&&!glovar.optionsActive) {
                    try {
                        FileReader fileReader = new FileReader("/Users/legitmichel777/Desktop/sweep/mineCom.txt");
                        if (fileReader.ready()) {
                            BufferedReader bufferedReader = new BufferedReader(fileReader);
                            if (bufferedReader.ready()) {
                                String rturnMsg = bufferedReader.readLine();
                                boolean isGud=true;
                                try {
                                    if (rturnMsg.isBlank()) continue;
                                } catch (NullPointerException ex) {
                                    isGud=false;
                                    System.out.println("ERROR!");
                                }
                                if (isGud) {
                                    if (rturnMsg.equals("MOVE")) {
                                        fileReader.close();
                                        bufferedReader.close();
                                        fileReader = new FileReader("/Users/legitmichel777/Desktop/sweep/mineComBig.txt");
                                        bufferedReader = new BufferedReader(fileReader);
                                        String disRd;
                                        while ((disRd = bufferedReader.readLine()) != null) {
                                            if (disRd.equals("MARK")) {
                                                int arg1, arg2;
                                                arg1 = Integer.parseInt(bufferedReader.readLine());
                                                arg2 = Integer.parseInt(bufferedReader.readLine());
                                                if (MinesweeperX.covered[arg2][arg1] != -1) {
                                                    grid.get(arg2).get(arg1).setIcon(MinesweeperX.mnpic[20]);
                                                    MinesweeperX.covered[arg2][arg1] = -1;
                                                    MinesweeperX.minesLeft--;
                                                    frame.repaint(arg1 * 32, arg2 * 32 + 70 + elmset, 32, 32);
                                                }
                                            } else if (disRd.equals("OPEN")) {
                                                int arg1, arg2;
                                                arg1 = Integer.parseInt(bufferedReader.readLine());
                                                arg2 = Integer.parseInt(bufferedReader.readLine());
                                                glovar.coord botin = new glovar.coord();
                                                botin.x = arg2;
                                                botin.y = arg1;
                                                glovar.uncovr.push(botin);
                                            } else if (disRd.equals("UNMARK")) {
                                                int arg1, arg2;
                                                arg1 = Integer.parseInt(bufferedReader.readLine());
                                                arg2 = Integer.parseInt(bufferedReader.readLine());
                                                if (MinesweeperX.covered[arg2][arg1] == -1) {
                                                    grid.get(arg2).get(arg1).setIcon(MinesweeperX.mnpic[18]);
                                                    MinesweeperX.covered[arg2][arg1] = 1;
                                                    MinesweeperX.minesLeft++;
                                                    frame.repaint(arg1 * 32, arg2 * 32 + 70 + elmset, 32, 32);
                                                }
                                            }
                                        }
                                        bufferedReader.close();
                                        fileReader.close();
                                        BufferedWriter ovrwrite;
                                        ovrwrite = new BufferedWriter(new FileWriter("/Users/legitmichel777/Desktop/sweep/mineCom.txt"));
                                        ovrwrite.write("nothing here:)");
                                        ovrwrite.close();
                                        break readBot;
                                    }
                                }
                            }
                        }
                        fileReader.close();
                    } catch (FileNotFoundException ex) {

                    } catch (IOException ex) {

                    }
                    try {
                        sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                while (!glovar.chorded.empty()) {
                    glovar.coord handl = glovar.chorded.pop();
                    int flagged = 0;
                    for (int k = 0; k < 8; k++) {
                        int nx = handl.x + dir[k][0];
                        int ny = handl.y + dir[k][1];
                        if (nx >= 0 && nx < glovar.szx && ny >= 0 && ny < glovar.szy) {
                            flagged += covered[nx][ny] == -1 ? 1 : 0;
                        }
                    }
                    if (flagged == mnnum[handl.x][handl.y]) {
                        for (int k = 0; k < 8; k++) {
                            int nx = handl.x + dir[k][0];
                            int ny = handl.y + dir[k][1];
                            if (nx >= 0 && nx < glovar.szx && ny >= 0 && ny < glovar.szy) {
                                if (covered[nx][ny] == 1) {
                                    glovar.coord toP = new glovar.coord();
                                    toP.x = nx;
                                    toP.y = ny;
                                    glovar.uncovr.push(toP);
                                }
                            }
                        }
                    }
                }
                while (!glovar.uncovr.empty()) {
                    glovar.coord handl = glovar.uncovr.pop();
                    if (glovar.virgin) {
                        timerActive=true;
                        timerStart= Instant.now();
                        //generate map
                        glovar.coord[] choose = new glovar.coord[glovar.szx * glovar.szy];
                        for (int i = 0; i < glovar.szx; i++) {
                            for (int j = 0; j < glovar.szy; j++) {
                                glovar.coord tmp = new glovar.coord();
                                tmp.x = i;
                                tmp.y = j;
                                choose[i * glovar.szy + j] = tmp;
                            }
                        }
                        ArrayList<glovar.coord> toshufl = new ArrayList(Arrays.asList(choose));
                        shuffle(toshufl, new Random(System.currentTimeMillis()));
                        int gotten = 0;
                        for (int i = 0; gotten < glovar.minecnt; i++) {
                            glovar.coord amn = toshufl.get(i);
                            if (Math.abs(handl.x - amn.x) > 2 || Math.abs(handl.y - amn.y) > 2) {
                                mnnum[amn.x][amn.y] = -1;
                                gotten++;
                            }
                        }
                        for (int i = 0; i < glovar.szx; i++) {
                            for (int j = 0; j < glovar.szy; j++) {
                                if (mnnum[i][j] != -1) {
                                    int surMn = 0;
                                    for (int k = 0; k < 8; k++) {
                                        int nx = i + dir[k][0];
                                        int ny = j + dir[k][1];
                                        if (nx >= 0 && nx < glovar.szx && ny >= 0 && ny < glovar.szy) {
                                            surMn += mnnum[nx][ny] == -1 ? 1 : 0;
                                        }
                                    }
                                    mnnum[i][j] = surMn;
                                }
                            }
                        }
                        glovar.virgin = false;
                    }
                    Stack<glovar.coord> toUpdt = new Stack();
                    if (mnnum[handl.x][handl.y] == -1) {
                        if (keepGoing) {
                            System.out.println("LOSS");
                            //GAME OVER
                            smiley.setIcon(mnpic[21]);
                            keepGoing = false;
                            toUpdt.push(handl);
                            covered[handl.x][handl.y] = 2; //covered=2:Show highlighted mine
                            for (int i = 0; i < glovar.szx; i++) {
                                for (int j = 0; j < glovar.szy; j++) {
                                    if (mnnum[i][j] == -1 && covered[i][j] == 1) {
                                        covered[i][j] = 0; //covered=0:show mine
                                        glovar.coord died = new glovar.coord();
                                        died.x = i;
                                        died.y = j;
                                        toUpdt.push(died);
                                    } else if (mnnum[i][j] != -1 && covered[i][j] == -1) {
                                        covered[i][j] = 3; //covered=3:Show error mine
                                        glovar.coord died = new glovar.coord();
                                        died.x = i;
                                        died.y = j;
                                        toUpdt.push(died);
                                    }
                                }
                            }
//                            MinesweeperX.updt();
                        }
                    } else {
                        Stack<glovar.coord> unAll = new Stack();
                        unAll.push(handl);
                        if (covered[handl.x][handl.y]==1) blocksLeft--;
                        covered[handl.x][handl.y] = 0;
                        boolean fursTime = true;
                        while (!unAll.empty()) {
                            if (fursTime) {
                                fursTime = false;
                                smiley.setIcon(mnpic[23]);
                                needsBak=true;
                            }
                            glovar.coord dis = unAll.pop();
                            toUpdt.push(dis);
                            if (mnnum[dis.x][dis.y] == 0) {
                                for (int k = 0; k < 8; k++) {
                                    int nx = dis.x + dir[k][0];
                                    int ny = dis.y + dir[k][1];
                                    if (nx >= 0 && nx < glovar.szx && ny >= 0 && ny < glovar.szy) {
                                        if (covered[nx][ny] == 1) {
                                            glovar.coord toP = new glovar.coord();
                                            toP.x = nx;
                                            toP.y = ny;
                                            covered[nx][ny] = 0;
                                            blocksLeft--;
                                            unAll.push(toP);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //repaint
                    if (blocksLeft == 0) {
                        if (keepGoing) {
                            keepGoing = false;
                            smiley.setIcon(mnpic[27]);
                            for (int i = 0; i < 3; i++) {
                                minel[i].setIcon(numcon[0]);
                            }
                            //win!
                            System.out.println("WIN");
//                            MinesweeperX.updt();
                            for (int i = 0; i < glovar.szx; i++) {
                                for (int j = 0; j < glovar.szy; j++) {
                                    if (covered[i][j] == 1 && mnnum[i][j] == -1) {
                                        covered[i][j] = -1;
                                        glovar.coord showAll = new glovar.coord();
                                        showAll.x = i;
                                        showAll.y = j;
                                        toUpdt.push(showAll);
                                    }
                                }
                            }
                        }
                    }
                    while (!toUpdt.empty()) {
                        glovar.coord toProc = toUpdt.pop();
                        if (covered[toProc.x][toProc.y] == -1) {
                            grid.get(toProc.x).get(toProc.y).setIcon(mnpic[20]);
                        } else if (covered[toProc.x][toProc.y] == 0) {
                            if (mnnum[toProc.x][toProc.y] == -1) {
                                grid.get(toProc.x).get(toProc.y).setIcon(mnpic[24]);
                            } else grid.get(toProc.x).get(toProc.y).setIcon(mnpic[mnnum[toProc.x][toProc.y]]);
                        } else if (covered[toProc.x][toProc.y] == 1) {
                            grid.get(toProc.x).get(toProc.y).setIcon(mnpic[18]);
                        } else if (covered[toProc.x][toProc.y] == 2) {
                            grid.get(toProc.x).get(toProc.y).setIcon(mnpic[25]);
                        } else if (covered[toProc.x][toProc.y] == 3) {
                            grid.get(toProc.x).get(toProc.y).setIcon(mnpic[26]);
                        }
                        frame.repaint(toProc.x * 32, toProc.y * 32 + 70+elmset, 32, 32);
                    }
                }
                try {
                    sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            rcvd = true;
        }
    }
    public static void updt() {
        keepGoing = false;
        nextGame=true;
    }
    public static void main(String args[]) {
        nextGame=true;
        for (int i=0;i<=8;i++) mnpic[i]=new ImageIcon(MinesweeperX.class.getResource(String.valueOf(i)+".png"));
        for (int i=9;i<=17;i++) mnpic[i]=new ImageIcon(MinesweeperX.class.getResource(String.valueOf(i-9)+"-clicked.png"));
        mnpic[18]=new ImageIcon(MinesweeperX.class.getResource("cover.png"));
        mnpic[19]=new ImageIcon(MinesweeperX.class.getResource("cover-clicked.png"));
        mnpic[20]=new ImageIcon(MinesweeperX.class.getResource("flagged.png"));
        mnpic[21]=new ImageIcon(MinesweeperX.class.getResource("ded.png"));
        mnpic[22]=new ImageIcon(MinesweeperX.class.getResource("smiley.png"));
        mnpic[23]=new ImageIcon(MinesweeperX.class.getResource("shock.png"));
        mnpic[24]=new ImageIcon(MinesweeperX.class.getResource("mine.png"));
        mnpic[25]=new ImageIcon(MinesweeperX.class.getResource("mine-clicked.png"));
        mnpic[26]=new ImageIcon(MinesweeperX.class.getResource("error-flag.png"));
        mnpic[27]=new ImageIcon(MinesweeperX.class.getResource("swag.png"));
        mnpic[28]=new ImageIcon(MinesweeperX.class.getResource("pref.png"));
        for (int i=0;i<=9;i++) numcon[i]=new ImageIcon(MinesweeperX.class.getResource("t"+i+".png"));
        numcon[10]=new ImageIcon(MinesweeperX.class.getResource("t-.png"));
        glovar.minecnt=10;
        glovar.chorded=new Stack<glovar.coord>();
        glovar.uncovr=new Stack<glovar.coord>();
        glovar.szx=9;
        glovar.szy=9;
        glovar.chord=true;
        frame = new JFrame("MinesweeperX Pro");
        new MinesweeperX();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_O) {
            if (glovar.optionsActive == false) {
                new Options();
                glovar.optionsActive = true;
            }
        } else if (e.getKeyCode()==KeyEvent.VK_SHIFT) {
            glovar.shiftDown = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_SHIFT) {
            glovar.shiftDown=false;
        }
    }
}
