package ai.qiwu.com.xiaoduhome.controller;

import ai.qiwu.com.xiaoduhome.common.Constants;
import ai.qiwu.com.xiaoduhome.pojo.data.SpiritUpdateCsvData;
import ai.qiwu.com.xiaoduhome.spirit.SpiritRedisService;
import com.alibaba.da.coin.ide.spi.meta.AskedInfoMsg;
import com.alibaba.da.coin.ide.spi.meta.ExecuteCode;
import com.alibaba.da.coin.ide.spi.meta.ResultType;
import com.alibaba.da.coin.ide.spi.security.RSAUtil;
import com.alibaba.da.coin.ide.spi.standard.ResultModel;
import com.alibaba.da.coin.ide.spi.standard.SecurityWrapperTaskQuery;
import com.alibaba.da.coin.ide.spi.standard.TaskQuery;
import com.alibaba.da.coin.ide.spi.standard.TaskResult;
import com.alibaba.da.coin.ide.spi.trans.MetaFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

import static ai.qiwu.com.xiaoduhome.common.Constants.PRIVATE_KEY;

/**
 * @author 苗权威
 * @dateTime 19-12-6 上午10:31
 */
@RestController
@RequestMapping("/spiritceshi")
@Slf4j
public class SpiritController {
//    private final SpiritService spiritService;

    private final SpiritRedisService spiritRedisService;

    @Autowired
    public SpiritController(SpiritRedisService spiritRedisService) {
        this.spiritRedisService = spiritRedisService;
    }

    /**
     * 利用天猫精灵打开发包解析请求并从构造响应数据返回
     * @param taskQuery 请求的参数信息
     * @return 返回响应的JSON数据
     */
    @PostMapping(value = "/") //智能小说
    public ResultModel<TaskResult> getResponse(@RequestBody String taskQuery) {
//        log.info("==========================");
        //        log.info("TaskQuery:{}",taskQuery);
        TaskQuery query = MetaFormat.parseToQuery(taskQuery);

        // build response
        ResultModel<TaskResult> resultModel = new ResultModel<>();
        TaskResult taskResult = spiritRedisService.getResponse(query,5);
//        log.info("taskResult:"+taskResult);
        resultModel.setReturnCode("0");
        resultModel.setReturnValue(taskResult);
//        log.info("天猫响应信息:"+ JSON.toJSONString(resultModel));

        return resultModel;
    }

    @PostMapping(value = "/smart") // 智能故事
    public ResultModel<TaskResult> smart(@RequestBody String taskQuery) {
        //        log.info("TaskQuery:{}",taskQuery);
        TaskQuery query = MetaFormat.parseToQuery(taskQuery);

        // build response
        ResultModel<TaskResult> resultModel = new ResultModel<>();
        TaskResult taskResult = spiritRedisService.getResponse(query,4);
//        log.info("taskResult:"+taskResult);
        resultModel.setReturnCode("0");
        resultModel.setReturnValue(taskResult);
//        log.info("天猫响应信息:"+ JSON.toJSONString(resultModel));

        return resultModel;
    }

    @PostMapping(value = "/encryption") // 智能小说加密版
    public ResultModel<TaskResult> story(@RequestBody String taskQuery) {
//        log.info("天猫请求信息:{}",taskQuery);
        TaskResult result;
        try {
            SecurityWrapperTaskQuery wrapperTaskQuery = MetaFormat.parseToWrapperQuery(taskQuery);
            String decryptStr = RSAUtil.decryptByPrivateKey(wrapperTaskQuery.getSecurityQuery(), PRIVATE_KEY);
            TaskQuery query = MetaFormat.parseToQuery(decryptStr);
            result = spiritRedisService.getResponse(query, 5);
        } catch (Exception e) {
            log.warn(ExceptionUtils.getStackTrace(e));
            result = bottomResponse(54808L);
        }

        // build response
        ResultModel<TaskResult> resultModel = new ResultModel<>();
        resultModel.setReturnCode("0");
        resultModel.setReturnValue(result);
//        log.info("天猫响应信息:"+ JSON.toJSONString(resultModel));

        return resultModel;
    }

    @PostMapping(value = "/smart/encryption") // 智能故事加密版
    public ResultModel<TaskResult> literature(@RequestBody String taskQuery) {
//        log.info("天猫请求信息:{}",taskQuery);
        TaskResult result;
        try {
            SecurityWrapperTaskQuery wrapperTaskQuery = MetaFormat.parseToWrapperQuery(taskQuery);
            String decryptStr = RSAUtil.decryptByPrivateKey(wrapperTaskQuery.getSecurityQuery(), PRIVATE_KEY);
//            log.info("====================================");
            TaskQuery query = MetaFormat.parseToQuery(decryptStr);
            result = spiritRedisService.getResponse(query, 4);
        } catch (Exception e) {
            log.warn(ExceptionUtils.getStackTrace(e));
            result = bottomResponse(37076L);
        }

        // build response
        ResultModel<TaskResult> resultModel = new ResultModel<>();
        resultModel.setReturnCode("0");
        resultModel.setReturnValue(result);
//        log.info("天猫响应信息:"+ JSON.toJSONString(resultModel));

        return resultModel;
    }

    @PostMapping("/update/csv")
    public void updateCsvMap(@RequestBody SpiritUpdateCsvData data) {
        String secret = data.getSecId();
        if (StringUtils.startsWith(secret, "mqw") && StringUtils.endsWith(secret, "#")
                && secret.length() == 8) {
            spiritRedisService.updateSpiritCsv();
        }
    }

//    @PostMapping(value = "/novel") // 智能小说
    public ResultModel<TaskResult> novel(@RequestBody String taskQuery) {
//        log.info("TaskQuery:{}",taskQuery);
        TaskQuery query = MetaFormat.parseToQuery(taskQuery);

        // build response
        ResultModel<TaskResult> resultModel = new ResultModel<>();
        TaskResult taskResult = spiritRedisService.getResponse(query,5);
//        logger.info("taskResult:"+taskResult);
        resultModel.setReturnCode("0");
        resultModel.setReturnValue(taskResult);

        return resultModel;
    }

//    @GetMapping("/mark")
//    public void updateMark(Integer type, Integer mark) {
//        if (type != null) {
//            switch (type) {
//                case 1: SpiritController.openRSA = mark; break;// 1 open
////                case 2: SpiritRedisService.mark = mark;break;
////                case 3: SpiritRedisService.manyId = mark;break;
//            }
//        }
//    }

    private static TaskResult bottomResponse(Long intentId) {
        TaskResult result = new TaskResult();
        result.setReply(Constants.ErrorMsg.SORRY_UNCATCH);
        result.setResultType(ResultType.ASK_INF);
        result.setExecuteCode(ExecuteCode.SUCCESS);

        result.setAskedInfos(Collections.singletonList(new AskedInfoMsg("any", intentId)));
        return result;
    }

}
