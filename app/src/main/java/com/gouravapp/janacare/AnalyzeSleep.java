package com.gouravapp.janacare;

import java.util.Calendar;
import java.util.List;

/**
 * Created by gaurav on 09/10/16.
 */
public class AnalyzeSleep {
    DatabaseHandler db=null;
    private List<TimeEntry> list_work = null;
    private long startTime,endTime;
    private String result = "";
    private Calendar real_time;
    public AnalyzeSleep(List<TimeEntry> list,long startTime, long endTime,Calendar real_time,DatabaseHandler db){
        this.list_work = list;
        this.endTime = endTime;
        this.startTime = startTime;
        this.real_time = real_time;
        this.db=db;
        this.analyze();
    }

    public void analyze(){
        long goodSleepTime = 0,badSleepTime=0,nearAwakeTime=0;
        int flag = 0;
        int last_sleep = 0;
        long last_time=0;
        for(TimeEntry t:this.list_work){
            int tot_sec = (int)(t.time/1000);
            int min = tot_sec/60;
            int hour = min/60;
            int sec = tot_sec%60;
            Calendar temp = (Calendar) real_time.clone();
            temp.add(Calendar.HOUR,hour);
            temp.add(Calendar.MINUTE,min);
            temp.add(Calendar.SECOND,sec);
            String time_now = ""+temp.get(Calendar.HOUR)+":"+temp.get(Calendar.MINUTE)+":"+temp.get(Calendar.SECOND);
            if(t.speed <= 15){
                goodSleepTime=goodSleepTime+(t.time-last_time);
                if(last_sleep>10){
                    flag=1;
                }
                if(flag==1){
                    this.result = this.result + "Good Sleep Started at: "+time_now+"\n";
                    //db.addEntry("GOOD SLEEP START: ",time_now);
                    flag=0;
                }

            }else
            if(t.speed > 15 && t.speed<100){
                badSleepTime+=(t.time-last_time);
                if(last_sleep>100 || last_sleep<10){
                    flag=1;
                }
                if(flag==1){
                    this.result = this.result + "OKish Sleep Started at: "+time_now+"\n";
                    //db.addEntry("OKish SLEEP START: ",time_now);
                    flag=0;
                }

            }else
            if(t.speed >= 100){
                nearAwakeTime+=(t.time-last_time);;
                if(last_sleep<100){
                    flag=1;
                }
                if(flag==1){
                    this.result = this.result + "Near Awake Started at: "+time_now+"\n";
                    //db.addEntry("YOU ARE NEARLY AWAKE: ",time_now);
                    flag=0;
                }
            }
            last_sleep = t.speed;
            this.endTime=t.time;
            last_time = t.time;
        }
        int tot_sec = (int)(this.endTime/1000);
        int good_sec = (int)(goodSleepTime/1000);
        int bad_sec = (int)(badSleepTime/1000);
        int wake_sec = (int)(nearAwakeTime/1000);
        String  total_sleep = ""+((tot_sec)/3600)+" hours, "+((tot_sec)/60)+" mins, "+((tot_sec)%60)+"seconds";
        String good_sleep = ""+((good_sec)/3600)+" hours, "+((good_sec)/60)+" mins, "+((good_sec)%60)+"seconds";
        String bad_sleep = ""+((bad_sec)/3600)+" hours, "+((bad_sec)/60)+" mins, "+((bad_sec)%60)+"seconds";
        String wake_sleep = ""+((wake_sec)/3600)+" hours, "+((wake_sec)/60)+" mins, "+((wake_sec)%60)+"seconds";
        String s = "Total Sleep Time : "+ total_sleep +
                "\n"+"Good Sleep TIme :"+ good_sleep+
                "\n"+"OKish Sleep Time :"+bad_sleep+
                "\n"+"Near Awake Time: "+wake_sleep+"\n";

//        List<String[]> list = db.getAllEntry();
//        String res = s;
//        for(String[] str:list){
//            res = res + str[1]+"  "+str[0]+"\n";
//        }


        this.result = s + this.result;
    }

    public String getResult(){
        return this.result;
    }


}
