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
package ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.display.templates;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * 分类列表模板
 * 
 * @author hujie08
 * @version 1.0
 * @since 2018年4月27日
 */
@JsonTypeName("ListTemplate4")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListTemplate4 extends BaseTemplate {

    // 列表项
    private ArrayList<ListItemWithListTemplate4> listItems = new ArrayList<>();

    /**
     * 构造函数
     */
    public ListTemplate4() {
    }

    /**
     * 获取列表项list的getter方法
     * 
     * @return ArrayList 列表项list
     */
    public ArrayList<ListItemWithListTemplate4> getListItems() {
        return listItems;
    }

    /**
     * 设置列表项列表的setter方法
     * 
     * @param listItems
     *            列表项list
     */
    public void setListItems(ArrayList<ListItemWithListTemplate4> listItems) {
        this.listItems = listItems;
    }

    /**
     * 添加列表项
     * 
     * @param listItem
     *            添加的列表项
     */
    public void addListItem(ListItemWithListTemplate4 listItem) {
        listItems.add(listItem);
    }
}