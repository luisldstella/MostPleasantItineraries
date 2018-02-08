package path;

import input.Query;

public abstract class PathResolver {
    public abstract int getMaximumNoise(Query query);
    int max(int max1, int max2) {
        return max1 > max2 ? max1 : max2;
    }
}
