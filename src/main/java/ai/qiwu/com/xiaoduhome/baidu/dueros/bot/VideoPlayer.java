/* 
 * Copyright (c) 2017 Baidu, Inc. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ai.qiwu.com.xiaoduhome.baidu.dueros.bot;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;

import ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.videoplayer.event.*;
import ai.qiwu.com.xiaoduhome.baidu.dueros.model.Request;
import ai.qiwu.com.xiaoduhome.baidu.dueros.model.Response;

/**
 * {@code VideoPlayer}继承自{@link ai.qiwu.com.xiaoduhome.baidu.dueros.bot.BaseBot}类，用于处理端上报的videoplayer事件
 * 
 * @author hujie08(hujie08@baidu.com)
 * @version V1.1.1
 * @since v1.1.1
 */
public class VideoPlayer extends BaseBot {

    /**
     * 构造函数， 使用反序列后的request作为参数
     * 
     * @param request
     *            反序列化后的Request
     * @throws IOException
     *             抛出的异常
     */
    public VideoPlayer(Request request) throws IOException {
        super(request);
    }

    /**
     * 构造方法，使用request字符串作为参数
     * 
     * @param request
     *            Request序列化后的字符串
     * @throws IOException
     *             抛出的异常
     */
    public VideoPlayer(String request) throws IOException {
        super(request);
    }

    /**
     * 构造方法，使用HttpServletRequest对象作为参数
     * 
     * @param request
     *            HttpServletRequest对象
     * @throws IOException
     *             抛出的异常
     */
    public VideoPlayer(HttpServletRequest request) throws IOException {
        super(request);
    }

    /**
     * 处理PlaybackStartedEvent事件
     * 
     * @param playbackStartedEvent
     *            PlaybackStartedEvent事件
     * @return Response 返回的Response
     */
    protected ai.qiwu.com.xiaoduhome.baidu.dueros.model.Response onPlaybackStartedEvent(final PlaybackStartedEvent playbackStartedEvent) {
        return response;
    }

    /**
     * 处理PlaybackStoppedEvent事件
     * 
     * @param playbackStoppedEvent
     *            PlaybackStoppedEvent事件
     * @return Response 返回的Response
     */
    protected ai.qiwu.com.xiaoduhome.baidu.dueros.model.Response onPlaybackStoppedEvent(final PlaybackStoppedEvent playbackStoppedEvent) {
        return response;
    }

    /**
     * 处理onPlaybackNearlyFinishedEvent事件
     * 
     * @param playbackNearlyFinishedEvent
     *            PlaybackNearlyFinishedEvent事件
     * @return Response 返回的Response
     */
    protected ai.qiwu.com.xiaoduhome.baidu.dueros.model.Response onPlaybackNearlyFinishedEvent(final PlaybackNearlyFinishedEvent playbackNearlyFinishedEvent) {
        return response;
    }

    /**
     * 处理PlaybackFinishedEvent事件
     * 
     * @param playbackFinishedEvent
     *            PlaybackFinishedEvent事件
     * @return Response 返回的Response
     */
    protected ai.qiwu.com.xiaoduhome.baidu.dueros.model.Response onPlaybackFinishedEvent(final PlaybackFinishedEvent playbackFinishedEvent) {
        return response;
    }

    /**
     * 处理ProgressReportIntervalElapsedEvent事件
     * 
     * @param progressReportIntervalElapsedEvent
     *            ProgressReportIntervalElapsedEvent事件
     * @return Response 返回的Response
     */
    protected ai.qiwu.com.xiaoduhome.baidu.dueros.model.Response onProgressReportIntervalElapsedEvent(
            final ProgressReportIntervalElapsedEvent progressReportIntervalElapsedEvent) {
        return response;
    }

    /**
     * 处理ProgressReportDelayElapsedEvent事件
     * 
     * @param progressReportDelayElapsedEvent
     *            ProgressReportDelayElapsedEvent事件
     * @return Response 返回的Response
     */
    protected ai.qiwu.com.xiaoduhome.baidu.dueros.model.Response onProgressReportDelayElapsedEvent(
            final ProgressReportDelayElapsedEvent progressReportDelayElapsedEvent) {
        return response;
    }

    /**
     * 处理PlaybackStutterStartedEvent事件
     * 
     * @param playbackStutterStartedEvent
     *            PlaybackStutterStartedEvent事件
     * @return Response 返回的Response
     */
    protected ai.qiwu.com.xiaoduhome.baidu.dueros.model.Response onPlaybackStutterStartedEvent(final PlaybackStutterStartedEvent playbackStutterStartedEvent) {
        return response;
    }

    /**
     * 处理PlaybackStutterFinishedEvent事件
     * 
     * @param playbackStutterFinishedEvent
     *            PlaybackStutterFinishedEvent事件
     * @return Response 返回的Response
     */
    protected ai.qiwu.com.xiaoduhome.baidu.dueros.model.Response onPlaybackStutterFinishedEvent(final PlaybackStutterFinishedEvent playbackStutterFinishedEvent) {
        return response;
    }

    /**
     * 处理PlaybackPausedEvent事件
     * 
     * @param playbackPausedEvent
     *            PlaybackPausedEvent事件
     * @return Response 返回的Response
     */
    protected ai.qiwu.com.xiaoduhome.baidu.dueros.model.Response onPlaybackPausedEvent(final PlaybackPausedEvent playbackPausedEvent) {
        return response;
    }

    /**
     * 处理PlaybackResumedEvent事件
     * 
     * @param playbackResumedEvent
     *            PlaybackResumedEvent事件
     * @return Response 返回的Response
     */
    protected ai.qiwu.com.xiaoduhome.baidu.dueros.model.Response onPlaybackResumedEvent(final PlaybackResumedEvent playbackResumedEvent) {
        return response;
    }

    /**
     * 处理PlaybackQueueClearedEvent事件
     * 
     * @param playbackQueueClearedEvent
     *            PlaybackQueueClearedEvent事件
     * @return Response 返回的Response
     */
    protected Response onPlaybackQueueClearedEvent(final PlaybackQueueClearedEvent playbackQueueClearedEvent) {
        return response;
    }
}
