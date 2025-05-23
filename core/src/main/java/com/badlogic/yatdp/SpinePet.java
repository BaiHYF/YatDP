package com.badlogic.yatdp;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.Logger;
import com.esotericsoftware.spine.*;
import com.esotericsoftware.spine.utils.TwoColorPolygonBatch;

/**
 * 封装桌面宠物中 Spine 模型与动画的相关操作。
 *
 * <p>{@code SpinePet} 类旨在对 Spine 模型的加载、渲染、动画控制等功能进行封装，
 * 提供简洁且统一的接口供桌宠应用程序调用。通过此类，可以轻松集成和管理桌面宠物的外观及行为。</p>
 *
 * <h3>主要功能</h3>
 * <ul>
 *     <li>加载 Spine 模型资源（支持 `.skel` 二进制格式）</li>
 *     <li>更新模型状态并渲染到桌面</li>
 *     <li>提供资源释放接口防止内存泄漏</li>
 * </ul>
 *
 * <h3>典型用法</h3>
 * <pre>
 * // 初始化
 * SpinePet pet = new SpinePet("models", "cat");
 *
 * // 在 render() 方法中更新和渲染
 * pet.render(Gdx.graphics.getDeltaTime());
 *
 * // 程序退出时释放资源
 * pet.dispose();
 * </pre>
 *
 * @author baiheyufei <BaiHeYuFei@outlook.com>
 * @see #SpinePet(String, String)
 * @see #render(float)
 * @see #dispose()
 */
public class SpinePet {
    Logger logger = new Logger("SpinePet", Logger.DEBUG);
    OrthographicCamera camera;
    TwoColorPolygonBatch batch;
    Skeleton skeleton;
    SkeletonData skeletonData;
    SkeletonBinary skeletonBinary;
//    SkeletonJson skeletonJson;
    SkeletonRenderer skeletonRenderer;
    TextureAtlas atlas;
    AnimationStateData animationStateData;
    AnimationState animationState;

    String defaultAnimationName = "Relax";
    String onClickedAnimationName = "Interact";
    private boolean isPlayingSpecialAnimation = false;
    float modelScale = 0.3f;

    /**
     * 构造函数，初始化 Spine 模型和渲染组件
     *
     * @param modelDirPath  模型文件所在目录路径（相对于 assets 目录）
     * @param modelFileName 模型文件名（不带扩展名）,需保持Spine模型的`.atlas`, `.skel`, `.png` 名称一致
     * @see #loadSpineModel(String, String)
     * @see #configureSkeleton()
     */
    public SpinePet(String modelDirPath, String modelFileName) {
        Gdx.app.setLogLevel(Logger.INFO);

        camera = new OrthographicCamera();
        batch = new TwoColorPolygonBatch();
        skeletonRenderer = new SkeletonRenderer();
        skeletonRenderer.setPremultipliedAlpha(true);

        loadSpineModel(modelDirPath, modelFileName);
        configureSkeleton();
        initAnimation();

        logger.info("Initialized.");
    }

    /**
     * 渲染方法，执行动画更新和模型绘制
     *
     * @param delta 时间增量，用于动画帧率控制
     */
    public void render(float delta) {
        logger.debug("Rendering ...");

        // 更新各种状态
        animationState.update(delta);
        animationState.apply(skeleton);
        skeleton.update(delta);
        skeleton.updateWorldTransform();
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // 渲染
        batch.begin();
        skeletonRenderer.draw(batch, skeleton);
        batch.end();
    }

    /**
     * 释放所有原生资源
     *
     * <p>在程序退出时调用以避免内存泄漏</p>
     */
    public void dispose() {
        atlas.dispose();
        batch.dispose();
    }

    public void resize() {
        camera.setToOrtho(false);
    }

    public void onClicked() {
        // TODO 添加点击事件处理逻辑
        // 切换到动画 `onClickedAnimationName`, 播放一次，不循环。结束后回到动画 `defaultAnimationName`
        if (isPlayingSpecialAnimation) {
            return;
        }

        logger.info("Clicked. Play Animation: " + onClickedAnimationName);
        isPlayingSpecialAnimation = true;
        animationState.setAnimation(0, onClickedAnimationName, false);
        animationState.addAnimation(0, defaultAnimationName, true, 0f);

        animationState.addListener(new AnimationState.AnimationStateAdapter() {
            @Override
            public void complete(AnimationState.TrackEntry entry) {
                if (entry.getAnimation().getName().equals(onClickedAnimationName)) {
                    isPlayingSpecialAnimation = false;
                }
            }
        });
    }



    private void loadSpineModel(String modelDirPath, String modelFileName) {
        atlas = new TextureAtlas(Gdx.files.internal(modelDirPath + "/" + modelFileName + ".atlas"));
        skeletonBinary = new SkeletonBinary(atlas);
        // `skeletonBinary` 的设置需在 `readSkeletonData()` 之前
        skeletonBinary.setScale(modelScale);

        skeletonData = skeletonBinary.readSkeletonData(Gdx.files.internal(modelDirPath + "/" + modelFileName + ".skel"));
        skeleton = new Skeleton(skeletonData);
        logger.info("Spine model loaded.");
    }

    private void configureSkeleton() {
        // 更新骨骼世界变换以获取最新边界
        skeleton.updateWorldTransform();

        Vector2 min = new Vector2();
        Vector2 max = new Vector2();
        FloatArray polygon = new FloatArray();

        // 获取边界数据
        skeleton.getBounds(min, max, polygon);

        // 屏幕适配计算
        float screenWidth = Gdx.graphics.getWidth();
        skeleton.setX(screenWidth / 2); // 水平居中
    }

    private void initAnimation() {
        // Parse animations
        Array<Animation> animations = skeletonData.getAnimations();
        animationStateData = new AnimationStateData(skeletonData);

        // 设置不同动画的混合过渡时间为 0.1sec
        for (int i = 0; i < animations.size; i++) {
            Animation animation1 = animations.get(i);
            logger.info(animation1.getName());
            for (int j = i + 1; j < animations.size; j++) {
                Animation animation2 = animations.get(j);
                animationStateData.setMix(animation1, animation2, 0.1f);
            }
        }

        animationState = new AnimationState(animationStateData);
        animationState.setAnimation(0, defaultAnimationName, true);
    }

//    public boolean isPlayingSpecialAnimation() {
//        return isPlayingSpecialAnimation;
//    }
//
//    public void setPlayingSpecialAnimation(boolean playingSpecialAnimation) {
//        isPlayingSpecialAnimation = playingSpecialAnimation;
//    }
}
