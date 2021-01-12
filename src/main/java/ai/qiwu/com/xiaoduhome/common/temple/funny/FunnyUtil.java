package ai.qiwu.com.xiaoduhome.common.temple.funny;

import ai.qiwu.com.xiaoduhome.pojo.dpl.Event;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-9-2 下午2:31
 */
public class FunnyUtil {

    private static final FunnyPojo PAGER_ITEM = getPageItem();

    public static FunnyDocument getFunnyDocument() {
        FunnyDocument document = new FunnyDocument();
        FunnyMainTemplate mainTemplate = new FunnyMainTemplate();
        document.setMainTemplate(mainTemplate);
        getMainItem(mainTemplate.getItems());
        return document;
    }

    private static void getMainItem(List<FunnyPojo> items) {
        // background image
        FunnyPojo background = new FunnyPojo();
        background.setType("Image");
        background.setSrc("https://aliyun-hz1.chewrobot.com/xiaoduhome/image/backend.jpg");
        background.setHeight("100%");
        background.setWidth("100%");
        background.setPosition("absolute");
        background.setLeft("0dp");
        background.setTop("0dp");

        //pager container
        FunnyPojo pageContainer = new FunnyPojo();
        pageContainer.setType("Container");
        pageContainer.setWidth("100%");
        pageContainer.setHeight("100%");

        FunnyPojo pager = new FunnyPojo();
        pager.setType("Pager");
        pager.setDirection("horizontal");
        pager.setComponentId("funnyPage");
        pager.setWidth("100%");
        pager.setHeight("100%");
        Event onLoadEvent = new Event("AutoPage", "funnyPage");
        onLoadEvent.setDurationInMillisecond(3000);
        pager.setOnLoaded(Collections.singletonList(onLoadEvent));

        pageContainer.setItems(Collections.singletonList(pager));

        // audio
        FunnyPojo audio = new FunnyPojo();
        audio.setType("Audio");
        audio.setComponentId("audioCom");
        audio.setLooping("false");
        audio.setAutoplay("true");

        // footer container
        FunnyPojo footerContainer = new FunnyPojo();
        footerContainer.setType("Container");
        footerContainer.setPosition("absolute");
        footerContainer.setWidth("100%");
        footerContainer.setLeft("0");
        footerContainer.setBottom("0");
        FunnyPojo footer = new FunnyPojo();
        footer.setType("Footer");
        footer.setHints(Arrays.asList("返回","上一页","下一页"));
        footerContainer.setItems(Collections.singletonList(footer));

        items.add(background);
        items.add(pageContainer);
        items.add(audio);
        items.add(footerContainer);
    }

    private static FunnyPojo getPageItem() {
        // container
        FunnyPojo pojo = new FunnyPojo();
        pojo.setType("Container");
        pojo.setWidth("100%");
        pojo.setHeight("100%");
        pojo.setMarginTop("40dp");

        // image,text container
        FunnyPojo imgTxtContainer = new FunnyPojo();
        imgTxtContainer.setType("Container");
        imgTxtContainer.setFlexDirection("row");

        // image
        FunnyPojo image = new FunnyPojo();
        image.setType("Image");
        image.setComponentId("${data.animId}");
        image.setSrc("${data.imgSrc}");
        image.setMarginTop("30dp");
        image.setHeight("300dp");
        image.setWidth("300dp");
        image.setMarginLeft("25dp");
        image.setScaleType("fitXY");
        image.setBorderRadius("20dp");
        Event imageEvent1 = new Event("SendEvent", "${data.imgId}");
        Event animEvent1 = new Event("Animation", "${data.animId}");
        animEvent1.setFrom("1");
        animEvent1.setTo("0.6");
        animEvent1.setEasing("ease-out");
        animEvent1.setAttribute("scaleX");
        animEvent1.setDuration(1500L);
        animEvent1.setRepeatCount("1");
        Event animEvent2 = new Event("Animation", "${data.animId}");
        animEvent2.setFrom("1");
        animEvent2.setTo("0.6");
        animEvent2.setEasing("ease-out");
        animEvent2.setAttribute("scaleY");
        animEvent2.setDuration(1500L);
        animEvent2.setRepeatCount("1");
        image.setOnClick(Arrays.asList(imageEvent1, animEvent1, animEvent2));

        // head,content container
        FunnyPojo textContainer = new FunnyPojo();
        textContainer.setType("Container");
        textContainer.setWidth("500dp");
        textContainer.setHeight("100%");
        textContainer.setMarginLeft("20dp");

        FunnyPojo head = new FunnyPojo();
        head.setType("Text");
        head.setText("${data.head}");
        head.setFontSize("35dp");
        head.setLineHeight("60dp");
        head.setMarginTop("20dp");
        head.setMarginLeft("10dp");

        FunnyPojo text = new FunnyPojo();
        text.setType("Text");
        text.setText("${data.text}");
        text.setFontSize("25dp");
        text.setLineHeight("60dp");
        text.setMarginTop("10dp");
        text.setMarginLeft("10dp");

        textContainer.setItems(Arrays.asList(head, text));

        imgTxtContainer.setItems(Arrays.asList(image, textContainer));

        // frame
        FunnyPojo frame = new FunnyPojo();
        frame.setType("Frame");
        frame.setWidth("300dp");
        frame.setHeight("50dp");
        frame.setMarginTop("15dp");
        frame.setMarginLeft("25dp");
        frame.setBorderWidth("1dp");
        frame.setBorderRadius("20dp");
        frame.setPaddingVertical("6dp");
        frame.setPaddingHorizontal("60dp");
        frame.setBorderColor("rgb(255,255,255,0.4)");
        FunnyPojo frameText = new FunnyPojo();
        frameText.setType("Text");
        frameText.setFontSize("20dp");
        frameText.setText("点击图片查看详情");
        frameText.setColor("rgb(255,255,255)");
        frame.setItems(Collections.singletonList(frameText));

        pojo.setItems(Arrays.asList(imgTxtContainer, frame));
        return pojo;
    }

    public static FunnyPojo getPagerItem() {
        return PAGER_ITEM;
    }

    public static FunnyPojo getPagerCom(FunnyDocument document) {
        return document.getMainTemplate().getItems().get(1).getItems().get(0);
    }
}
