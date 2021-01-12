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

package ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive;

import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.audioplayer.RenderAudioList;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.display.Hint;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.display.PushStack;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.display.RenderAlbumList;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.display.RenderTemplate;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.pay.Charge;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.permission.AskForPermissionsConsent;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.videoplayer.ClearQueue;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.videoplayer.RenderVideoList;
import com.baidu.dueros.data.response.directive.ConfirmIntent;
import com.baidu.dueros.data.response.directive.ConfirmSlot;
import com.baidu.dueros.data.response.directive.Delegate;
import com.baidu.dueros.data.response.directive.ElicitSlot;
import com.baidu.dueros.data.response.directive.Play;
import com.baidu.dueros.data.response.directive.Stop;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * 指令
 * 
 * @author tianlonglong(tianlong02@baidu.com)
 * @version V1.0
 * @since 2017年10月5日
 */
@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(ConfirmIntent.class), @JsonSubTypes.Type(ConfirmSlot.class),
        @JsonSubTypes.Type(Delegate.class), @JsonSubTypes.Type(ElicitSlot.class),
        @JsonSubTypes.Type(Play.class),
        @JsonSubTypes.Type(Stop.class),
        @JsonSubTypes.Type(Hint.class),
        @JsonSubTypes.Type(RenderTemplate.class),
        @JsonSubTypes.Type(ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.audioplayer.Play.class),
        @JsonSubTypes.Type(ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.audioplayer.Stop.class),
        @JsonSubTypes.Type(ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.videoplayer.Play.class),
        @JsonSubTypes.Type(ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.videoplayer.Stop.class),
        @JsonSubTypes.Type(ClearQueue.class),
        @JsonSubTypes.Type(Charge.class),
        @JsonSubTypes.Type(AskForPermissionsConsent.class),
        @JsonSubTypes.Type(PushStack.class),
        @JsonSubTypes.Type(RenderAudioList.class), @JsonSubTypes.Type(RenderVideoList.class),
        @JsonSubTypes.Type(RenderAlbumList.class),
        @JsonSubTypes.Type(SendPart.class)})
public abstract class Directive {

}
