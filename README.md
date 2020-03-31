#GXPMaster
# 基于Netty的通用报文交换平台
# 本平台适合于TCP Socket连接请求的各种特殊报文格式读取和解析

##目前该平台支持按以下几种报文格式的解析：
# 按行解析
# 定长包解析
# 固定分隔符解析
# 带报文头长度的各种报文解析

##GXPMaster Parameter 参数配置说明
###Server端参数
目前支持四种ServerMode
# 固定分隔符解析：servermode=org.gxpmaster.platform.splicing.delimiter.DelimiterServer
# 带报文头长度的各种报文解析：servermode=org.gxpmaster.platform.splicing.lengthfieldbase.LengthFieldBaseServer
# 定长包解析：servermode=org.gxpmaster.platform.splicing.fixedlength.FixedLengthServer
# 按行解析：servermode=org.gxpmaster.platform.splicing.linebase.LineBaseServer

服务器端口
serverport=9999

####LengthFieldBaseServer
# lengthfieldlength=2
# maxframelength=65535
# lengthfieldoffset=0
# lengthadjustment=0
# initialbytestostrip=2

####DelimiterServer
# delimitersymbol=@

####FixedLengthServer
# fixedlength=100

###Client端参数，测试用
# serverip=127.0.0.1