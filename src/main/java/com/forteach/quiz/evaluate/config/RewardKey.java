package com.forteach.quiz.evaluate.config;

public class RewardKey {

    /**
     * 奖励类型为小红花
     */
    public static final String REWARD_KEY = "Flower";

    /**
     * 奖励类型为小红花
     */
    public static final String REWARD_FLOWER_KEY = "Flower";


    /**
     * 获得小红花的学生。
     *
     * @param circleId
     * @param rewardType 奖励类型
     * @return
     */
    public static String rewardAddKey(final String circleId, final String rewardType) {
        return circleId.concat(REWARD_KEY.concat(rewardType));
    }
}
