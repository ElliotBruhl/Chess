public abstract class SPRTTest { //this is broken :(

    private static final double ALPHA = .01; //significance level
    private static final double BETA = .9; //power

    private static final double HO_WIN = .3;
    private static final double HO_DRAW = .4;
    private static final double HO_LOSS = .3;

    private static final double HA_WIN = .4;
    private static final double HA_DRAW = .35;
    private static final double HA_LOSS = .25;

    private static final double LOW_BOUND = Math.log((1-BETA)/ALPHA);
    private static final double UPP_BOUND = Math.log(BETA / (1 - ALPHA));
    private static final double WIN_INC = Math.log(HA_WIN / HO_WIN);
    private static final double DRAW_INC = Math.log(HA_DRAW / HO_DRAW);
    private static final double LOSS_INC = Math.log(HA_LOSS / HO_LOSS);

    private static double llr = 0.0; //log-likelihood ratio

    public static void UpdateSRPT(int result) {
        switch (result) {
            case 1 -> llr+=WIN_INC;
            case 0 -> llr+=DRAW_INC;
            case -1 -> llr+=LOSS_INC;
        }
    }
    public static int getSRPT() {
        if (llr > UPP_BOUND) return 1;
        else if (llr < LOW_BOUND) return -1;
        return 0;
    }
}
