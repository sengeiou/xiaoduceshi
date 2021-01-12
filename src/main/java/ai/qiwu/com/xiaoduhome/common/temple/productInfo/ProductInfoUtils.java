package ai.qiwu.com.xiaoduhome.common.temple.productInfo;

import ai.qiwu.com.xiaoduhome.pojo.dpl.Event;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author 苗权威
 * @dateTime 19-9-23 下午5:37
 */
public class ProductInfoUtils {

    private static final ProductInfoPojo BACK_IMG = buildProductInfoBackgroundImg();
    private static final ProductInfoPojo HEADER = buildProductInfoHead();
    private static final ProductInfoPojo FOOTER = buildProductInfoFooter();

    public static ProductInfoDocument getProductINfoDocument(String src, String productName, String time, String author, String labelText,
                                                             String introText, String startComponId, String flowerId, String flowerText,
                                                             String collectId, String collectSrc, String collectText) {
        ProductInfoDocument document = new ProductInfoDocument();
        ProductInfoMainTemplate mainTemplate = new ProductInfoMainTemplate();
        document.setMainTemplate(mainTemplate);
        document.setDuration(120000);
        mainTemplate.getItems().addAll(Arrays.asList(BACK_IMG, HEADER, buildLeftRightPart(src, productName, time,
                author, labelText, introText, startComponId, flowerId, flowerText, collectId, collectSrc, collectText), FOOTER));
        return document;
    }

    public static ProductInfoPojo buildAudioComponent(String src) {
        ProductInfoPojo audio = new ProductInfoPojo();
        audio.setType("Audio");
        audio.setComponentId("audioCom");
//        audio.setLooping("false");
//        audio.setAutoplay("true");
        audio.setSrc(src);
        audio.setOnEnd(Collections.singletonList(new Event("SendEvent","audio")));
        return audio;
    }

    private static ProductInfoPojo buildLeftRightPart(String src, String productName, String time, String author, String labelText,
                                                      String introText, String startComponId, String flowerId, String flowerText,
                                                      String collectId, String collectSrc, String collectText) {
        ProductInfoPojo lrCon = new ProductInfoPojo();
        lrCon.setType("Container");
        lrCon.setFlexDirection("row");
        lrCon.setWidth("100%");
        lrCon.setHeight("100%");
        lrCon.setItems(Arrays.asList(buildLeftCoverImage(src), buildRightPart(productName, time, author, labelText,
                introText, startComponId, flowerId, flowerText, collectId, collectSrc, collectText)));
        return lrCon;
    }

    private static ProductInfoPojo buildLeftCoverImage(String src) {
        ProductInfoPojo coverImage = new ProductInfoPojo();
        coverImage.setType("Image");
        coverImage.setWidth("280dp");
        coverImage.setHeight("280dp");
        coverImage.setBorderRadius("15dp");
        coverImage.setScaleType("fitXY");
        coverImage.setMarginLeft("50dp");
        coverImage.setMarginTop("12dp");
        coverImage.setComponentId("imgPro");
        coverImage.setSrc(src);

        Event rotateAnimation = new Event("Animation", "imgPro");
        rotateAnimation.setFrom("0");
        rotateAnimation.setTo("360");
        rotateAnimation.setEasing("ease-out");
        rotateAnimation.setAttribute("rotation");
        rotateAnimation.setDuration(2000L);
        rotateAnimation.setRepeatCount("1");
        rotateAnimation.setRepeatMode("reverse");

        coverImage.setOnClick(Collections.singletonList(rotateAnimation));
        return coverImage;
    }

    private static ProductInfoPojo buildRightPart(String productName, String time, String author, String labelText,
                                                  String introText, String startComponId, String flowerId, String flowerText,
                                                  String collectId, String collectSrc, String collectText) {
        ProductInfoPojo rightPartCon = new ProductInfoPojo();
        rightPartCon.setType("Container");
        rightPartCon.setFlexDirection("column");
        rightPartCon.setWidth("600dp");
        rightPartCon.setHeight("100%");
        rightPartCon.setItems(Arrays.asList(buildProductName(productName), buildTimeAuthor(time, author), buildLabel(labelText),
                buildIntro(introText), buildIconCon(startComponId, flowerId, flowerText, collectId, collectSrc, collectText)));
        return rightPartCon;
    }

    private static ProductInfoPojo buildProductName(String productName) {
        ProductInfoPojo name = new ProductInfoPojo();
        name.setType("Text");
        name.setMarginTop("10dp");
        name.setFontSize("38dp");
        name.setMarginLeft("5%");
        name.setText(productName);
        return name;
    }

    private static ProductInfoPojo buildTimeAuthor(String time, String author) {
        ProductInfoPojo timeAuthorCon = new ProductInfoPojo();
        timeAuthorCon.setType("Container");
        timeAuthorCon.setMarginTop("10dp");
        timeAuthorCon.setFlexDirection("row");

        ProductInfoPojo timePojo = new ProductInfoPojo();
        timePojo.setType("Text");
        timePojo.setMarginLeft("5%");
        timePojo.setFontSize("22dp");
        timePojo.setText(time);

        ProductInfoPojo authorPojo = new ProductInfoPojo();
        authorPojo.setType("Text");
        authorPojo.setMarginLeft("10%");
        authorPojo.setFontSize("22dp");
        authorPojo.setText(author);

        timeAuthorCon.setItems(Arrays.asList(timePojo, authorPojo));
        return timeAuthorCon;
    }

    private static ProductInfoPojo buildLabel(String labelText) {
        ProductInfoPojo labelCon = new ProductInfoPojo();
        labelCon.setType("Container");
        labelCon.setMarginTop("6dp");
        labelCon.setFlexDirection("row");

        ProductInfoPojo label = new ProductInfoPojo();
        label.setType("Text");
        label.setMarginLeft("5%");
        label.setTextOverflow("ellipsis");
        label.setFontSize("22dp");
        label.setMaxLines(1);
        label.setText(labelText);

        labelCon.setItems(Collections.singletonList(label));
        return labelCon;
    }

    private static ProductInfoPojo buildIntro(String introText) {
        ProductInfoPojo introCon = new ProductInfoPojo();
        introCon.setType("ScrollView");
        introCon.setHeight("120dp");
        introCon.setDirection("vertical");
        introCon.setMarginTop("30dp");
        introCon.setMarginRight("26dp");

        ProductInfoPojo intro = new ProductInfoPojo();
        intro.setType("Text");
        intro.setMarginLeft("5%");
        intro.setFontSize("25dp");
        //intro.setLineHeight("20dp");
        //intro.setMarginTop("30dp");
        //intro.setMaxLines(3);
        //intro.setTextOverflow("ellipsis");
        intro.setText(introText);

        introCon.setItems(Collections.singletonList(intro));
        return introCon;
    }

    private static ProductInfoPojo buildIconCon(String startComponId, String flowerId, String flowerText,
                                                String collectId, String collectSrc, String collectText) {
        ProductInfoPojo iconCon = new ProductInfoPojo();
        iconCon.setType("Container");
        iconCon.setPosition("absolute");
        iconCon.setFlexDirection("row");
        iconCon.setBottom("170dp");
        iconCon.setItems(Arrays.asList(buildStart(startComponId), buildFlower(flowerId, flowerText),
                buildCollect(collectId, collectSrc, collectText)));
        return iconCon;
    }

    private static ProductInfoPojo buildStart(String startComponId) {
        ProductInfoPojo startCon = new ProductInfoPojo();
        startCon.setType("Container");
        startCon.setAlignItems("center");
        startCon.setMarginLeft("50dp");
        startCon.setOnClick(Collections.singletonList(new Event("SendEvent", startComponId)));

        ProductInfoPojo image = new ProductInfoPojo();
        image.setType("Image");
        image.setHeight("60dp");
        image.setWidth("60dp");
        image.setSrc("https://hw-gz25.heyqiwu.cn/duai/image/feel.png");
        image.setScaleType("fitCenter");

        ProductInfoPojo startText = new ProductInfoPojo();
        startText.setType("Text");
        startText.setText("开始体验");
        startText.setMarginTop("10dp");
        startText.setFontSize("20dp");

        startCon.setItems(Arrays.asList(image, startText));
        return startCon;
    }

    private static ProductInfoPojo buildFlower(String flowerId, String flowerText) {
        ProductInfoPojo flowerCon = new ProductInfoPojo();
        flowerCon.setType("Container");
        flowerCon.setAlignItems("center");
        flowerCon.setMarginLeft("120dp");
        flowerCon.setOnClick(Collections.singletonList(new Event("SendEvent", flowerId)));

        ProductInfoPojo image = new ProductInfoPojo();
        image.setType("Image");
        image.setHeight("60dp");
        image.setWidth("60dp");
        image.setSrc("https://hw-gz25.heyqiwu.cn/duai/image/flower.png");
        image.setScaleType("fitCenter");

        ProductInfoPojo flower = new ProductInfoPojo();
        flower.setType("Text");
        flower.setComponentId("pft");
        flower.setText(flowerText);
        flower.setMarginTop("10dp");
        flower.setFontSize("20dp");

        flowerCon.setItems(Arrays.asList(image, flower));
        return flowerCon;
    }

    private static ProductInfoPojo buildCollect(String collectId, String collectSrc, String collectText) {
        ProductInfoPojo collectCon = new ProductInfoPojo();
        collectCon.setType("Container");
        collectCon.setAlignItems("center");
        collectCon.setMarginLeft("120dp");
        collectCon.setComponentId(collectId);
        collectCon.setOnClick(Collections.singletonList(new Event("SendEvent", collectId)));

        ProductInfoPojo image = new ProductInfoPojo();
        image.setType("Image");
        image.setHeight("60dp");
        image.setWidth("60dp");
        image.setComponentId("collectImage");
        image.setSrc(collectSrc);
        image.setScaleType("fitCenter");

        ProductInfoPojo collect = new ProductInfoPojo();
        collect.setType("Text");
        collect.setComponentId("collectText");
        collect.setText(collectText);
        collect.setMarginTop("10dp");
        collect.setFontSize("20dp");

        collectCon.setItems(Arrays.asList(image, collect));
        return collectCon;
    }

    private static ProductInfoPojo buildProductInfoFooter() {
        ProductInfoPojo footerCon = new ProductInfoPojo();
        footerCon.setType("Container");
        footerCon.setPosition("absolute");
        footerCon.setWidth("100%");
        footerCon.setLeft("0");
        footerCon.setBottom("0");

        ProductInfoPojo footer = new ProductInfoPojo();
        footer.setType("Footer");
        footer.setHints(Arrays.asList("开始","我要送花","收藏作品","回到首页","返回"));

        footerCon.setItems(Collections.singletonList(footer));

        return footerCon;
    }

    private static ProductInfoPojo buildProductInfoBackgroundImg() {
        ProductInfoPojo backGroundImg = new ProductInfoPojo();
        backGroundImg.setType("Image");
        backGroundImg.setPosition("absolute");
        backGroundImg.setTop("0dp");
        backGroundImg.setLeft("0dp");
        backGroundImg.setSrc("https://hw-gz25.heyqiwu.cn/duai/image/backNew.jpg");
        backGroundImg.setHeight("100%");
        backGroundImg.setWidth("100%");
        backGroundImg.setScaleType("fitXY");

        return backGroundImg;
    }

    private static ProductInfoPojo buildProductInfoHead() {
        ProductInfoPojo head = new ProductInfoPojo();
        head.setType("Header");
        head.setHeaderTitle("交游天下");
        head.setHasBackIcon(false);
        return head;
    }
}
