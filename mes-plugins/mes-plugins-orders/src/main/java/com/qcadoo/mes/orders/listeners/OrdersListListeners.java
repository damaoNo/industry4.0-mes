/**
 * ***************************************************************************
 * Copyright (c) 2018 RiceFish Limited
 * Project: SmartMES
 * Version: 1.6
 *
 * This file is part of SmartMES.
 *
 * SmartMES is Authorized software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.qcadoo.mes.orders.listeners;

import com.qcadoo.mes.orders.TechnologyServiceO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qcadoo.mes.orders.constants.OrderFields;
import com.qcadoo.mes.orders.constants.OrdersConstants;
import com.qcadoo.mes.orders.states.client.OrderStateChangeViewClient;
import com.qcadoo.mes.states.service.client.util.ViewContextHolder;
import com.qcadoo.mes.technologies.constants.TechnologyFields;
import com.qcadoo.mes.technologies.states.TechnologyStateChangeViewClient;
import com.qcadoo.mes.technologies.states.constants.TechnologyStateStringValues;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.api.ViewDefinitionState;
import com.qcadoo.view.api.components.GridComponent;

@Service
public class OrdersListListeners {

    @Autowired
    private OrderStateChangeViewClient orderStateChangeViewClient;

    @Autowired
    private TechnologyStateChangeViewClient technologyStateChangeViewClient;

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private TechnologyServiceO technologyServiceO;

    public void changeState(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        GridComponent gridComponent = (GridComponent) view.getComponentByReference("grid");
        for (Long orderId : gridComponent.getSelectedEntitiesIds()) {
            Entity order = getOrderDD().get(orderId);
            Entity technology = order.getBelongsToField(OrderFields.TECHNOLOGY);
            if (TechnologyStateStringValues.DRAFT.equals(technology.getStringField(TechnologyFields.STATE))) {
                technologyStateChangeViewClient.changeState(new ViewContextHolder(view, state), TechnologyStateStringValues.ACCEPTED, technology);
            } else if (TechnologyStateStringValues.CHECKED.equals(technology.getStringField(TechnologyFields.STATE))) {
                technologyServiceO.changeTechnologyStateToAccepted(technology);
            }
            orderStateChangeViewClient.changeState(new ViewContextHolder(view, state), args[0], order);
        }
    }

    private DataDefinition getOrderDD() {
        return dataDefinitionService.get(OrdersConstants.PLUGIN_IDENTIFIER, OrdersConstants.MODEL_ORDER);
    }
}