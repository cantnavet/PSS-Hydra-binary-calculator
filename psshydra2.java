/**
 * PSS Hydra 2进制
 * 
 * @author cantnavet
 * @version 1.0
 */


public class psshydra2 {

    public static int[][] f = new int[10000][2];    //{d,n} d为深度(最外层为1)，n为pn(例如{1,1}就是BMS的(0,0))
    public static int a = 0;    //始终指向最前那位的位置
    public static int w;  //记录当前用来比对的对象所在位置
    public static int l;  //层差
    public static boolean isequal;  //该操作是否生效，如是则再次循环
    public static boolean did; //该方法是否生效过，如是则再次呼叫另一个方法

    /*add number特殊玩法
    f[a] = new int[]{1,1};                                      每次+1
    f[a] = new int[]{2,1};                                      每次在第2层+1
    f[a] = new int[]{2,2};                                      每次在第2层放一个p2
    f[a] = new int[]{(f[a-1][0]> X ? X :f[a-1][0]),1};          每次都在公式末尾项不大于X的最大层+1   p.s.X为11时需2151下到达P4，估计与P3的2^2059有大关系
    f[a] = new int[]{f[a-1][0],1};                              每次都在公式末尾项同层+1
    f[a] = new int[]{f[a-1][0]+1,1};                            每次都在公式末尾项深一层+1 (等于prss)
    f[a] = new int[]{f[a-1][0],(f[a-1][1]>1?2:1)};              当末尾项大于P1时在公式末尾项同层放P2，否则放P1
    f[a] = new int[]{f[a-1][0],(f[a-1][1]>= X ? X :f[a-1][1])};   当末尾项大于PX时在公式末尾项同层放PX，否则放Pn
    f[a] = new int[]{f[a-1][0],(f[a-1][1]==1?1:f[a-1][1]-1)};   在公式末尾项同层放原公式末尾项的Pn-1，当该项为p1时为p1
    f[a] = new int[]{f[a-1][0],f[a-1][1]};                      在公式末尾项同层放原公式末尾项的Pn (其实同样等于prss)
    f[a] = new int[]{f[a-1][0]+1,(f[a-1][1]==1?1:f[a-1][1]-1)}; 在公式末尾项深一层放原公式末尾项的Pn-1，当该项为p1时为p1
    f[a] = new int[]{f[a-1][0]+1,f[a-1][1]};                    在公式末尾项深一层放原公式末尾项的Pn (不是，你想干什么)

    果糕玩意
    f[a] = new int[]{f[a-1][0],(f[a-1][1]%2==1?f[a-1][1]:f[a-1][1]-1)};               当末尾项Pn为奇数放置Pn，偶数放置Pn-1 (?)
    f[a] = new int[]{f[a-1][0],(f[a-1][1]==1?1:f[a-1][1]-(int)(Math.random()+0.5))};  在公式末尾项同层随机放原公式末尾项的Pn或Pn-1，当该项为p1时为p1 (??)
    f[a] = new int[]{f[a-1][0],(f[a-1][1]==1?1:1+(int)(f[a-1][1]*Math.random()))};    在公式末尾项同层放原公式末尾项的Pn-P1随机整数，当该项为p1时为p1 (???)
    f[a] = new int[]{f[a-1][0]+1,(f[a-1][1]%2==1?f[a-1][1]:f[a-1][1]-1)};             当末尾项Pn为奇数在深一层放置Pn，偶数放置Pn-1 (????)
    f[a] = new int[]{f[a-1][0]+1,(f[a-1][1]==1?1:1+(int)(f[a-1][1]*Math.random()))};  在公式末尾项深一层放原公式末尾项的Pn-P1随机整数，当该项为p1时为p1 (?????)
    */

    //主要设定(可以改这里的东西)
    public static boolean steps = true;       //是否展示收束步骤(pss hydra)
    public static double sleeptime = 1;       //每次步骤的间隔时间（秒）
    public static int skipsteps = 1;          //修改此项来决定每n个表达式输出一次，1就是每个都输出
    public static int runtimes = 1000;        //运行次数
    //初始表达式，n为pn，d为深度 (0为没有该项，最小深度和n为1)
    public static String n = "123121";
    public static String d = "123122";

    public static void main(String[] args) {
        int sleeptimems = (int)(sleeptime*1000);
        a=n.length()-1;
        
        for (int i=0;i<n.length();i++){
            f[i] = new int[]{(d.charAt(i)-'0'),(n.charAt(i)-'0')};
        }

        printp();

        for (int i = 0; i < runtimes; i++) {

            a++;                                        //move pointer

            //add a number to last term (唯二可以动的地方)
            f[a] = new int[]{1,1};

            toNormal();                                 //main 2进制进位 method, 将序列f合并至最简化

            if(i%skipsteps==0){printp();}               //print ans, 更改i%x 做到隔x步输出一次公式

            try {                                       //输出间隔
                Thread.sleep(sleeptimems); 
            } catch (InterruptedException e) {
                // 恢复中断状态
                Thread.currentThread().interrupt();
            }

            //符合需求print并停止(可选)            
            // if(f[a][1]==4){
            //     printp();
            //     System.out.println("i "+i);
            //     break;
            // }  
        }
    }

    //将一个展开的公式收缩
    public static void toNormal(){
        equalpart();
        did = false;
        insidepart();
        while (did) {
            did = false;
            equalpart();
            insidepart();
        }
    }

    //pn(..+pn(..)) to pn(..+pn+1(0)) part
    public static void insidepart(){
        int j;
        isequal = true;
        while (isequal) {
            isequal = false;
            for (int w = a; w >= 0; w--) {
                j=w-(a-w)-1;
                if (j<0)break;
                if (f[j][0] < f[j+1][0] && f[j][0] < f[w][0]){
                    l=f[w][0] - f[j][0];
                    for (int k = 0; k<w-j ; k++){

                        if(f[j+k][0]+l != f[w+k][0] || f[j+k][1] != f[w+k][1] || f[j+k][1]<f[j][1]){
                            break;
                        }
                        if (k==w-j-1){
                            if (steps) {
                                printp();
                                System.out.print(" = ");
                            }
                            f[w][1]++;
                            for (int l = w+1; l<=a; l++){
                                f[l] = new int[]{0, 0};
                            }
                            a = w; 
                            isequal = true;
                            did = true;
                        }
                    }
                    if(isequal)break;
                }
            }
        }
    }

    //pnX+pnX to pn(X+p1) part
    public static void equalpart(){
        isequal = true;
        while (isequal) {
            isequal = false;
            w=a;
            for (int j = a-1; j >= 0; j--) {
                if (f[j][0] < f[w][0]){
                    w=j;
                    continue;
                }
                if (f[j][0] == f[w][0]){
                    for (int k = 0; k<w-j ; k++){
                        if(f[j+k][0] != f[w+k][0] || f[j+k][1] != f[w+k][1]){
                            break;
                        }
                        if (k==w-j-1){
                            if (steps) {
                                printp();
                                System.out.print(" = ");
                            }
                            f[w] = new int[]{f[w][0]+1,1};
                            for (int l = w+1; l<=a; l++){
                                f[l] = new int[]{0, 0};
                            }
                            a = w; 
                            isequal = true;
                            did = true;
                        }
                    }
                    if(isequal)break;
                }
            }
        }
    }

    //以BMS形式print
    public static void printb(int[][] f){
        for (int i=0;i<=f.length;i++){
            if (f[i][0] != 0){
                System.out.print(f[i][0]);
            }else{
                break;
            }
            
        }
        System.out.println();
        for (int i=0;i<=f.length;i++){
            if (f[i][1] != 0){
                System.out.print(f[i][1]);
            }else{
                break;
            }
        }
        System.out.println();
        System.out.println("-----------------------------");
    }

    //以PSS hydra形式print
    public static void printp(){
        for (int i=0;i<=f.length;i++){
            if (f[i][0] != 0){
                System.out.print("P"+f[i][1]);
                if (f[i+1][0]>f[i][0]){
                    System.out.print("(");
                }
                if (f[i+1][0]==f[i][0]){
                    System.out.print("+");
                }
                if (f[i+1][0]<f[i][0]){
                    for (int j=0;j<f[i][0]-f[i+1][0] -(f[i+1][0]==0?1:0);j++){
                        System.out.print(")");
                    }
                    if (f[i+1][0]!=0){
                        System.out.print("+");
                    }
                }
            }else{
                break;
            }
        }
        System.out.println();
    }
}
