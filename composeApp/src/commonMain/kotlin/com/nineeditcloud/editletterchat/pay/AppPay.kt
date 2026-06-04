package com.nineeditcloud.editletterchat.pay

/*应用支付-微信/支付宝/QQ
PayReq(构建支付请求的类)各参数作用
参数	         类型	作用说明
appId	     String	你的微信开放平台应用ID，用于识别是哪个App发起支付
partnerId	 String	商户号(mch_id)，你的商户平台账号
prepayId	 String	预支付交易会话ID，由服务端调用“统一下单”接口后返回，微信用它来关联本次支付请求
nonceStr	 String	随机字符串，防重放
timeStamp	 String	时间戳(秒级)，用于签名和时效校验
packageValue String	固定值 "Sign=WXPay"，扩展字段，不能改
sign	     String	签名，把以上参数按规则拼接后加密，防止参数被篡改
signType	 String	签名算法(如MD5、HMAC-SHA256)，非必填但建议加上
extData	     String	可选扩展字段，安卓有时用来传自定义数据，但官方不建议依赖它传递业务信息
逻辑：
客户端创建支付请求时，要向服务端发送商品ID，等待反馈 预支付交易会话ID、签名
服务端生成 商户订单号(out_trade_no)、微信支付订单号(transaction_id)、根据商品ID查找价格信息，
服务端将 商户订单号(out_trade_no)、商品ID、是否已支付状态 写入数据库记录
服务端调用 微信-统一下单接口，传入商户订单号和金额(total_fee)等，得到 预支付交易会话ID(prepay_id)，将prepay_id、签名等反馈给客户端
客户端用完整参数构建支付请求，调起微信支付通道发送支付请求
服务端监听微信收款消息后，根据商户订单号(out_trade_no) 确定哪条订单已支付，并将数据库中对应订单的支付状态改为true
*/
class AppPay{

}