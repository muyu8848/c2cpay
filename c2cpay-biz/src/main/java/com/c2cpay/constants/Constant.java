package com.c2cpay.constants;

import java.util.Arrays;
import java.util.List;

public class Constant {
	
	public static final String 转账业务类型_C2B = "C2B";
	
	public static final String 转账业务类型_C2C = "C2C";
	
	public static final String 转账业务类型_B2C = "B2C";
	
	public static final String 支付成功 = "1";

	public static final String 支付失败 = "0";
	
	public static final String 通知成功返回值 = "success";

	public static final String 通知失败返回值 = "fail";
	
	public static final String 通知状态_未通知 = "1";

	public static final String 通知状态_通知成功 = "2";

	public static final String 通知状态_通知失败 = "3";

	public static final String 通知状态_无需通知 = "4";

	public static final String 发送短信 = "sendSms";

	public static final String 短信发送状态_未发送 = "1";

	public static final String 短信发送状态_发送成功 = "2";

	public static final String 短信发送状态_发送失败 = "3";
	
	public static final String 商户收款异步通知 = "merchantReceiptAsynNotice";

	public static final String 收款资金同步 = "receiptFundSync";
	
	public static final String 商户收款资金同步 = "merchantReceiptFundSync";
	
	public static final String 商户收款超时未拉起 = "merchantReceiptTimeOutUnLock";
	
	public static final String 商户收款超时未付款 = "merchantReceiptTimeOutUnPaid";
	
	public static final String 收款记录状态_未拉起 = "1";
	
	public static final String 收款记录状态_未付款 = "2";
	
	public static final String 收款记录状态_已完成 = "3";
	
	public static final String 收款记录状态_超时未拉起 = "4";
	
	public static final String 收款记录状态_超时未付款 = "5";
	
	public static final String 转账记录状态_已完成 = "3";
	
	public static final String 短信限制 = "lock";

	public static final String 短信类型_验证码_修改支付密码 = "modifyPayPwd";

	public static final String 短信类型_验证码_注册 = "register";

	public static final String 短信类型_验证码_忘记密码 = "forgetLoginPwd";

	public static final String 订单状态变动消息_待接单 = "订单创建成功";

	public static final String 订单状态变动消息_接单已取消 = "未接单超时取消";

	public static final String 订单状态变动消息_接单已拒绝 = "卖方拒绝接单";

	public static final String 订单状态变动消息_未付款 = "已接单";

	public static final String 订单状态变动消息_已付款 = "订单已被标记为已付款";

	public static final String 订单状态变动消息_已完成 = "订单已完成";

	public static final String 订单状态变动消息_已取消 = "订单已取消";

	public static final String 订单状态变动消息_申诉中 = "订单申诉中";

	public static final String 聊天消息类型_文字 = "text";

	public static final String 风控处罚_限制买入 = "limitBuy";

	public static final String 风控处罚_限制卖出 = "limitSell";

	public static final String 风控原因_买方取消交易 = "buyerCancelTrade";

	public static final String 风控原因_卖方拒绝接单 = "sellerRejectOrder";

	public static final String 风控原因_买方未付款 = "buyerUnPaid";

	public static final String 风控原因_使用非本人支付账户付款 = "notMySelfPaid";

	public static final String 风控原因_已付款卖方未放行 = "paidSellerUnConfirm";

	public static final String 操作方_系统 = "system";

	public static final String 操作方_客服 = "customerService";

	public static final String 操作方_卖方 = "seller";

	public static final String 操作方_买方 = "buyer";

	public static final String 申诉状态_待处理 = "1";

	public static final String 申诉状态_已完成 = "2";

	public static final String 申诉处理方式_撤销申诉 = "1";

	public static final String 申诉处理方式_已完成交易 = "2";

	public static final String 申诉处理方式_取消交易 = "3";

	public static final String 交易申诉类型_已付款卖方未放行 = "1";

	public static final String 交易申诉类型_钱付多了 = "2";

	public static final String 交易申诉类型_买方未付款 = "3";

	public static final String 交易申诉类型_钱付少了 = "4";

	public static final String 交易申诉类型_使用非本人支付账户付款 = "5";

	public static final List<String> 买方申诉类型 = Arrays.asList(交易申诉类型_已付款卖方未放行, 交易申诉类型_钱付多了);

	public static final List<String> 卖方申诉类型 = Arrays.asList(交易申诉类型_买方未付款, 交易申诉类型_钱付少了, 交易申诉类型_使用非本人支付账户付款);

	public static final String 未读公告同步 = "unreadNoticeSync";

	public static final String 未读公告 = "unreadNotice";

	public static final String 会员余额变动日志类型_买入 = "1";

	public static final String 会员余额变动日志类型_卖出 = "2";

	public static final String 会员余额变动日志类型_挂单卖出 = "3";

	public static final String 会员余额变动日志类型_取消交易 = "4";

	public static final String 会员余额变动日志类型_挂单撤销 = "5";

	public static final String 会员余额变动日志类型_转账 = "6";

	public static final String 会员余额变动日志类型_收款 = "7";

	public static final String 会员余额变动日志类型_系统 = "8";
	
	public static final String 商户余额变动日志类型_系统 = "1";
	
	public static final String 商户余额变动日志类型_转账 = "2";
	
	public static final String 商户余额变动日志类型_收款 = "3";

	public static final String 交易订单状态_待接单 = "1";

	public static final String 交易订单状态_接单已取消 = "2";

	public static final String 交易订单状态_接单已拒绝 = "3";

	public static final String 交易订单状态_未付款 = "4";

	public static final String 交易订单状态_已付款 = "5";

	public static final String 交易订单状态_已完成 = "6";

	public static final String 交易订单状态_已取消 = "7";

	public static final String 交易订单状态_申诉中 = "8";

	public static final String 预交易订单状态_进行中 = "1";

	public static final String 预交易订单状态_已完成 = "2";

	public static final String 预交易订单状态_已撤单 = "3";

	public static final String 订单交易类型_出售 = "sell";

	public static final String 订单交易类型_购买 = "buy";

	public static final String 收付款信息_银行卡 = "bankCard";

	public static final String 收付款信息_微信 = "wechat";

	public static final String 收付款信息_支付宝 = "alipay";

	public static final String 菜单类型_一级菜单 = "menu_1";

	public static final String 菜单类型_二级菜单 = "menu_2";

	public static final String 子系统_会员端 = "member";

	public static final String 子系统_商户端 = "merchant";

	public static final String 子系统_后台管理 = "admin";

	public static final String 登录提示_登录成功 = "登录成功";

	public static final String 登录状态_成功 = "1";

	public static final String 登录状态_失败 = "0";

	public static final String 功能状态_启用 = "1";

	public static final String 功能状态_禁用 = "0";

}
