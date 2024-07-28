public class MoveGenV2 implements Player {
    
    @Override
    public int[] getMove(Board b) {
        return new int[]{0};
    }
    @Override
    public int getPromotionBB() {
        return 0;
    }

}