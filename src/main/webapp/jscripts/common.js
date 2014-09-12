function trim(str){	
	var pos=0;
	while (pos<str.length && str.charAt(pos)==" "){
		str = str.substr(1, str.length);
	}
	pos = str.length-1;
	while (pos>=0 && pos<str.length && str.charAt(pos)==" "){
		str = str.substr(0, str.length-1);
		pos = str.length-1;
	}
	return str;
}

function isIntegerNumber(str){
	var pattern="1234567890";
	for (var i=0; i<str.length; i++){
		var pos = 0;
		for (var j=0; j<pattern.length; j++){
			if (str.charAt(i)==pattern.charAt(j)){
				pos=1; 
				break;
			}
		}
		if(pos==0) 
			return false;
	}
	if (str.length>1 && str.charAt(0)=="0") 
		return false;
	return true;
}

function isDoubleNumber(str){
	var pattern="1234567890.";
	var pointCount = 0;
	for (var i=0; i<str.length; i++){
		var pos = 0;
		for (var j=0; j<pattern.length; j++){
			if (str.charAt(i)==pattern.charAt(j)){
				pos=1; 
				break;
			}
		}
		if(pos==0) 
			return false;
		if (str.charAt(i)==".")
			pointCount++;
	}
	if (str.length>1 && str.charAt(0)=="0") 
		return false;
	if (str.charAt(0)=="." || str.charAt(str.length-1)==".")
		return false;
	if (pointCount>1)
		return false;
	return true;
}

function isDataCorrect(y, m, d){
	if ( (m==1 || m==3 || m==5 || m==7 || m==8 || m==10 || m==12) && d>=1 && d<=31){
		return true;
	}
	if ((m==4 || m==6 || m==9 || m==11) && d>=1 && d<=30){
		return true;
	}
	if (m==2){
		if (y%4==0){
			if (d>=1 && d<=28)
				return true;
		} else {
			if (d>=1 && d<=29)
				return true;
		}
	}
	return false;
}	