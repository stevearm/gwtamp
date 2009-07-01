<?php
function printRpcGoodResponse($dataArray) {
	echo '{"'.GwtSharedConstants::$RPC_RESPONSE_KEY.'":{"'.GwtSharedConstants::$RPC_RESPONSE_STATUS_KEY.'":0,"'.GwtSharedConstants::$RPC_RESPONSE_DATA_KEY.'":['.implode(',', $dataArray).']}}';
}

function printRpcErrorResponse($errorCode, $errorString) {
	echo '{"'.GwtSharedConstants::$RPC_RESPONSE_KEY.'":{"'.GwtSharedConstants::$RPC_RESPONSE_STATUS_KEY.'":'.$errorCode.',"'.GwtSharedConstants::$RPC_RESPONSE_MESSAGE_KEY.'":"'.$errorString.'"}}';
}
?>
