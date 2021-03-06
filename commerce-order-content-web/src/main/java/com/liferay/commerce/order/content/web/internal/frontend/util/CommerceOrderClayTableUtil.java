/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.commerce.order.content.web.internal.frontend.util;

import com.liferay.commerce.account.model.CommerceAccount;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.content.web.internal.model.Order;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletQName;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.text.DateFormat;
import java.text.Format;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceOrderClayTableUtil {

	public static long getCommerceAccountId(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		CommerceContext commerceContext =
			(CommerceContext)httpServletRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		long commerceAccountId = 0;

		CommerceAccount commerceAccount = commerceContext.getCommerceAccount();

		if (commerceAccount != null) {
			commerceAccountId = commerceAccount.getCommerceAccountId();
		}

		return commerceAccountId;
	}

	public static String getEditOrderURL(
			long commerceOrderId, ThemeDisplay themeDisplay)
		throws PortalException {

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		PortletURL portletURL = PortletURLFactoryUtil.create(
			themeDisplay.getRequest(), portletDisplay.getId(),
			themeDisplay.getPlid(), PortletRequest.RENDER_PHASE);

		portletURL.setParameter("mvcRenderCommandName", "editCommerceOrder");
		portletURL.setParameter(
			"commerceOrderId", String.valueOf(commerceOrderId));

		portletURL.setParameter(
			PortletQName.PUBLIC_RENDER_PARAMETER_NAMESPACE + "backURL",
			themeDisplay.getURLCurrent());

		return portletURL.toString();
	}

	public static List<Order> getOrders(
			List<CommerceOrder> commerceOrders, ThemeDisplay themeDisplay,
			boolean editable)
		throws PortalException {

		List<Order> orders = new ArrayList<>();

		for (CommerceOrder commerceOrder : commerceOrders) {
			String amount = StringPool.BLANK;

			CommerceMoney totalMoney = commerceOrder.getTotalMoney();

			if (totalMoney != null) {
				amount = totalMoney.format(themeDisplay.getLocale());
			}

			Format dateFormat = FastDateFormatFactoryUtil.getDate(
				DateFormat.MEDIUM, themeDisplay.getLocale(),
				themeDisplay.getTimeZone());

			String url;

			if (editable) {
				url = getEditOrderURL(
					commerceOrder.getCommerceOrderId(), themeDisplay);
			}
			else {
				url = getOrderViewDetailURL(
					commerceOrder.getCommerceOrderId(), themeDisplay);
			}

			orders.add(
				new Order(
					commerceOrder.getCommerceOrderId(),
					commerceOrder.getCommerceAccountName(),
					dateFormat.format(commerceOrder.getCreateDate()),
					commerceOrder.getUserName(),
					CommerceOrderConstants.getOrderStatusLabel(
						commerceOrder.getOrderStatus()),
					WorkflowConstants.getStatusLabel(commerceOrder.getStatus()),
					amount, url));
		}

		return orders;
	}

	public static String getOrderViewDetailURL(
			long commerceOrderId, ThemeDisplay themeDisplay)
		throws PortalException {

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		PortletURL portletURL = PortletURLFactoryUtil.create(
			themeDisplay.getRequest(), portletDisplay.getId(),
			themeDisplay.getPlid(), PortletRequest.RENDER_PHASE);

		portletURL.setParameter(
			"mvcRenderCommandName", "viewCommerceOrderDetails");
		portletURL.setParameter(
			"commerceOrderId", String.valueOf(commerceOrderId));

		portletURL.setParameter(
			PortletQName.PUBLIC_RENDER_PARAMETER_NAMESPACE + "backURL",
			themeDisplay.getURLCurrent());

		return portletURL.toString();
	}

	public static String getViewShipmentURL(
			long commerceOrderItemId, ThemeDisplay themeDisplay)
		throws Exception {

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		PortletURL portletURL = PortletURLFactoryUtil.create(
			themeDisplay.getRequest(), portletDisplay.getId(),
			themeDisplay.getPlid(), PortletRequest.RENDER_PHASE);

		portletURL.setParameter(
			"mvcRenderCommandName", "viewCommerceOrderShipments");
		portletURL.setParameter(
			"commerceOrderItemId", String.valueOf(commerceOrderItemId));
		portletURL.setWindowState(LiferayWindowState.POP_UP);

		portletURL.setParameter(
			PortletQName.PUBLIC_RENDER_PARAMETER_NAMESPACE + "backURL",
			themeDisplay.getURLCurrent());

		return portletURL.toString();
	}

}