package ai.qiwu.com.xiaoduhome.service;

import ai.qiwu.com.xiaoduhome.common.BaseHolder;
import ai.qiwu.com.xiaoduhome.repository.primary.AppFavoriteTBRepository;


/**
 * 存储之前留存的代码，以备后用
 * @author 苗权威
 * @dateTime 19-9-24 上午10:49
 */
public class RunnableService {
    static class UnCollectThread implements Runnable {
        final String botId;
        final String userId;

        UnCollectThread(String botId, String userId) {
            this.botId = botId;
            this.userId = userId;
        }

        @Override
        public void run() {
            AppFavoriteTBRepository repository = BaseHolder.getBean("appFavoriteTBRepository");
            repository.deleteByBotIdAndUserId(botId, userId);
        }
    }

//    class SendFlowerBuyToXiaoduTB implements Runnable {
//        BuyEvent buyEvent;
//
//        SendFlowerBuyToXiaoduTB(BuyEvent buyEvent) {
//            this.buyEvent = buyEvent;
//        }
//        @Override
//        public void run() {
//            try {
//                String baiduOrderReferenceId = buyEvent.getPayload().getBaiduOrderReferenceId();
//                long timeStamp = Long.parseLong(buyEvent.getTimestamp())*1000;
//                String botId = new String(Base64.getDecoder().decode(buyEvent.getToken()), StandardCharsets.UTF_8);
//                String userId = getSessionAttribute("userId");
//                Integer unitePrice = Integer.parseInt(Config.getProperty("unitPrice", Constants.UNIT_PRICE));
//                if (userId == null) userId = getUserIdByToken(getAccessToken());
//                if (userId == null) throw new Exception("未获取到用户id");
//                XiaoDuOrderTbRepository repository = BaseHolder.getBean("xiaoDuOrderTbRepository");
//                XiaoDuOrderTB orderTB = new XiaoDuOrderTB();
//                orderTB.setAppUserId(userId);
//                orderTB.setBotId(botId);
//                orderTB.setNumber(1);
//                orderTB.setIsBuy(true);
//                orderTB.setOrderId(baiduOrderReferenceId);
//                orderTB.setTimeStamp(timeStamp);
//                orderTB.setUnitPrice(unitePrice);
//                orderTB.setXiaoduUserId(getUserId());
//                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//                orderTB.setCreateTime(timestamp);
//                orderTB.setUpdateTime(timestamp);
//                XiaoDuOrderTB response = repository.save(orderTB);
//                if (response == null) throw new Exception("向interface_db中的xiaodu_order_tb插入数据失败");
//            } catch (Exception e) {
//                log.error("用户成功支付,xiaodu_order_tb操作出现错误:{}",e.toString());
//            }
//        }
//    }
//
//    class SendFlowerThread implements Runnable {
//        BuyEvent buyEvent;
//
//        SendFlowerThread(BuyEvent buyEvent) {
//            this.buyEvent = buyEvent;
//        }
//
//        @Override
//        public void run() {
//            //String baiduOrderReferenceId = buyEvent.getPayload().getBaiduOrderReferenceId();
//            String timeStamp = buyEvent.getTimestamp();
//            String botId = new String(Base64.getDecoder().decode(buyEvent.getToken()), StandardCharsets.UTF_8);
//            String userId = getSessionAttribute("userId");
//            int unitePrice = Integer.parseInt(Config.getProperty("unitPrice", Constants.UNIT_PRICE));
//            try {
//                if (userId == null) userId = getUserIdByToken(getAccessToken());
//                if (userId == null) throw new Exception("未获取到用户id");
//                AppFlowerTBRepository flowerRepository = BaseHolder.getBean("appFlowerTBRepository");
//                AppFlowerTB flowerTB = new AppFlowerTB();
//                flowerTB.setNumber(1);
//                flowerTB.setModule(0);
//                flowerTB.setBotName(botId);
//                flowerTB.setUserId(Integer.parseInt(userId));
//                flowerTB.setStatus(1);
//                flowerTB.setUnitPrice(unitePrice/100.0);
//                flowerTB.setCreateTime(new Timestamp(Long.parseLong(timeStamp)*1000));
//                flowerTB.setOrderId(0);
//                AppFlowerTB response = flowerRepository.save(flowerTB);
//                if (response == null) throw new Exception("向app_flower_tb中插入数据失败");
//            } catch (Exception e) {
//                log.error("用户成功支付,game_flower_tb操作出现错误:{}",e.toString());
//            }
//        }
//    }
}
