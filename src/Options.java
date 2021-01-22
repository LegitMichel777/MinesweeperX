import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.time.Duration;
import java.time.Instant;

import static java.lang.Thread.sleep;

public class Options extends JFrame implements ActionListener {
    JLabel tit,mpwidlbl,mpheilbl,mpminelbl,clickNumlbl,mineden,minedif;
    JButton conBot,okBtn;
    JCheckBox clickNum;
    JTextField widtf,heitf,minetf;
    JSlider widsld,heisld,minesld;
    int mineMax;
    void updtMineDens() {
        if (heisld.getValue()*widsld.getValue()!=0) {
            double mineDen = 1.0 * minesld.getValue() / (1.0 * widsld.getValue() * heisld.getValue());
            mineDen *= 1000;
            mineDen = Math.round(mineDen) / 10.0;
            String dif="";
            if (mineDen<=13.0) dif="Easy";
            else if (mineDen<=16.0) dif="Medium";
            else if (mineDen<=21.0) dif="Hard";
            else if (mineDen<=26.0) dif="Expert";
            else dif="Impossible";
            mineden.setText("Mine density:" + mineDen);
            minedif.setText("Difficulty:"+dif);
            mineMax=(int)(0.35*heisld.getValue()*widsld.getValue());
            minesld.setMaximum(mineMax);
            repaint();
        }
    }
    public Options() {
        setTitle("Options");
        setBounds(50,50,400,730);
        setLayout(null);
        setResizable(false);
        tit = new JLabel("Options");
        tit.setHorizontalAlignment(SwingConstants.CENTER);
        tit.setVerticalAlignment(SwingConstants.CENTER);
        tit.setHorizontalTextPosition(SwingConstants.CENTER);
        tit.setVerticalTextPosition(SwingConstants.CENTER);
        tit.setFont(glovar.titleFont);
        tit.setBounds(50,5,300,80);
        add(tit);
        conBot = new JButton("Connect to bot");
        conBot.setFont(glovar.normalFont);
        conBot.setBounds(50,500,300,80);
        conBot.addActionListener(this);
        if (MinesweeperX.connectedToBot) {
            conBot.setText("Disconnect");
        }
        add(conBot);
        mpwidlbl= new JLabel("Width");
        mpwidlbl.setFont(glovar.normalFont);
        mpwidlbl.setBounds(50,100,200,50);
        add(mpwidlbl);
        mpheilbl=new JLabel("Height");
        mpheilbl.setFont(glovar.normalFont);
        mpheilbl.setBounds(50,170,200,50);
        add(mpheilbl);
        mpminelbl=new JLabel("Mines");
        mpminelbl.setFont(glovar.normalFont);
        mpminelbl.setBounds(50,240,200,50);
        add(mpminelbl);
        clickNum=new JCheckBox();
        clickNumlbl=new JLabel("Click to reveal");
        clickNumlbl.setBounds(90,450,400,50);
        clickNumlbl.setFont(glovar.normalFont);
        clickNum.setBounds(50,450,400,50);
        add(clickNum);
        add(clickNumlbl);
        widtf=new JTextField();
        heitf=new JTextField();
        minetf=new JTextField();
        widtf.setFont(glovar.normalFont);
        heitf.setFont(glovar.normalFont);
        minetf.setFont(glovar.normalFont);
        widtf.setBounds(280,100,70,50);
        heitf.setBounds(280,170,70,50);
        minetf.setBounds(280,240,70,50);
        mineden=new JLabel();
        mineden.setBounds(50,310,400,50);
        mineden.setFont(glovar.normalFont);
        minedif=new JLabel();
        minedif.setBounds(50,380,400,50);
        minedif.setFont(glovar.normalFont);
        add(widtf);
        add(heitf);
        add(minetf);
        add(mineden);
        add(minedif);
        widsld=new JSlider(9,glovar.widcap,glovar.szx);
        heisld=new JSlider(9,glovar.heicap,glovar.szy);
        widsld.setBackground(Color.BLACK);
        mineMax=(int)(0.35*glovar.szx*glovar.szy);
        minesld=new JSlider(10,mineMax,glovar.minecnt);
        widsld.setBounds(150,100,120,50);
        heisld.setBounds(150,170,120,50);
        minesld.setBounds(150,240,120,50);
        add(widsld);
        add(heisld);
        add(minesld);
        heitf.setText(String.valueOf(glovar.szy));
        widtf.setText(String.valueOf(glovar.szx));
        minetf.setText(String.valueOf(glovar.minecnt));
        heisld.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (heitf.getText()!=String.valueOf(heisld.getValue())) {
                    heitf.setText(String.valueOf(heisld.getValue()));
                }
                updtMineDens();
            }
        });
        widsld.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (widtf.getText()!=String.valueOf(widsld.getValue())) {
                    widtf.setText(String.valueOf(widsld.getValue()));
                }
                updtMineDens();
            }
        });
        minesld.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (minetf.getText()!=String.valueOf(minesld.getValue())) {
                    minetf.setText(String.valueOf(minesld.getValue()));
                }
                updtMineDens();
            }
        });
        heitf.addActionListener(this);
        widtf.addActionListener(this);
        minetf.addActionListener(this);

        setVisible(true);
        okBtn= new JButton("OK");
        okBtn.setFont(glovar.titleFont);
        okBtn.setBounds(125,620,150,70);
        add(okBtn);
        okBtn.addActionListener(this);

        clickNum.setSelected(glovar.chord);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                glovar.optionsActive = false;
            }
        });
        updtMineDens();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==okBtn) {
            glovar.chord=clickNum.isSelected();
            glovar.szy=heisld.getValue();
            glovar.szx=widsld.getValue();
            glovar.minecnt=minesld.getValue();
            MinesweeperX.updt();
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        } else if (e.getSource()==heitf) {
            int conv=Integer.parseInt(heitf.getText());
            if (conv!=heisld.getValue()) {
                if (conv > glovar.heicap) {
                    conv = glovar.heicap;
                } else if (conv < 9) {
                    conv = 9;
                }
                heisld.setValue(conv);
                heitf.setText(String.valueOf(conv));
            }
            updtMineDens();
        } else if (e.getSource()==widtf) {
            int conv=Integer.parseInt(widtf.getText());
            if (conv!=widsld.getValue()) {
                if (conv > glovar.widcap) {
                    conv = glovar.widcap;
                } else if (conv < 9) {
                    conv = 9;
                }
                widsld.setValue(conv);
                widtf.setText(String.valueOf(conv));
            }
            updtMineDens();
        } else if (e.getSource()==minetf) {
            int conv=Integer.parseInt(minetf.getText());
            if (conv!=minesld.getValue()) {
                if (conv > mineMax) {
                    conv = mineMax;
                } else if (conv < 10) {
                    conv = 10;
                }
                minesld.setValue(conv);
                minetf.setText(String.valueOf(conv));
            }
            updtMineDens();
        } else if (e.getSource()==conBot) {
            if (MinesweeperX.connectedToBot) {
                conBot.setText("Connect to bot");
                try {
                    BufferedWriter killBot = new BufferedWriter(new FileWriter("/Users/legitmichel777/Desktop/sweep/botCom.txt"));
                    killBot.write("CLOSE");
                    killBot.close();
                    MinesweeperX.connectedToBot = false;
                } catch (IOException ex) {
                    System.out.println("Expected bot directory missing!");
                }
            } else {
                conBot.setText("Connecting...");
                BufferedWriter startBot = null;
                int writeKey=(int) System.currentTimeMillis();
                try {
                    startBot = new BufferedWriter(new FileWriter("/Users/legitmichel777/Desktop/sweep/botCom.txt"));
                    startBot.write("LOOK\n");
                    startBot.write(String.valueOf(writeKey));
                    startBot.close();
                } catch (IOException ex) {
                    System.out.println("Expected bot directory missing!");
                }
                Instant connTm=Instant.now();
                while (true) {
                    try {
                        FileReader fileReader = new FileReader("/Users/legitmichel777/Desktop/sweep/mineCom.txt");
                        if (fileReader.ready()) {
                            BufferedReader bufferedReader = new BufferedReader(fileReader);
                            if (bufferedReader.ready()) {
                                String rturnMsg = bufferedReader.readLine();
                                if (rturnMsg.isBlank()) continue;
                                if (rturnMsg.equals("ACK")) {
                                    rturnMsg = bufferedReader.readLine();
                                    if (rturnMsg.equals(String.valueOf(writeKey))) {
                                        MinesweeperX.connectedToBot = true;
                                    }
                                }
                            }
                        }
                        fileReader.close();
                    } catch (FileNotFoundException ex) {

                    } catch (IOException ex) {

                    }
                    if (Duration.between(connTm,Instant.now()).toMillis()>300) {
                        break;
                    }
                    try {
                        sleep(30);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                if (MinesweeperX.connectedToBot) {
                    try {
                        startBot = new BufferedWriter(new FileWriter("/Users/legitmichel777/Desktop/sweep/botCom.txt"));
                        startBot.write("HANDSHAKE\n");
                        startBot.write(String.valueOf(writeKey));
                        startBot.close();
                    } catch (IOException ex) {
                        System.out.println("Expected bot directory missing!");
                    }
                    conBot.setText("Disconnect");
                }
                else conBot.setText("Connection failed");
            }
        }
    }
}