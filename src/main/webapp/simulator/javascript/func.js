	function fnSelectall(lb){
    	var selTarget = document.getElementById(lb);
     	for(var i=0;i<selTarget.length;i++){
        	 selTarget.options[i].selected = true;
     	}
     	return true;
	}
	
	String.prototype.trim = function(){ 
		return this.replace(/(^\s*)|(\s*$)/g, ""); 
	};
		
	String.prototype.ltrim = function(){ 
		return this.replace(/(^\s*)/g,""); 
	};
		
	String.prototype.rtrim = function(){ 
			return this.replace(/(\s*$)/g,""); 
	};
			
	function fnAdd(lb,sName,sValue) {
		
		if (sName.trim() == '' || sValue.trim() == '') {
			alert("Please input value");
			return;
		}
		
        var oOption = document.createElement("option");
        oOption.appendChild(document.createTextNode(sName + ":" + sValue));
        
        if (arguments.length == 3) {
            oOption.setAttribute("value", sName + ":" + sValue);
        }
		
		document.getElementById(lb).appendChild(oOption);
    }
	
	function fnRemoveItem(lb){
         var selTarget = document.getElementById(lb);
		 
		 //??????
         if(selTarget.selectedIndex > -1) {
            for(var i=0;i<selTarget.options.length;i++) {
               if(selTarget.options[i].selected) {
                   selTarget.remove(i);
                    i = i - 1;//?????
               }
            }
         }
   } 
   
   function fnUp(lb) {   
       var sel = document.getElementById(lb);
       for(var i=1; i < sel.length; i++) { 
           //?????????????????i=1??
           if(sel.options[i].selected){
              if(!sel.options.item(i-1).selected) {//?????????????
                   var selText = sel.options[i].text;
                   var selValue = sel.options[i].value;
                          
                   sel.options[i].text = sel.options[i-1].text;
                   sel.options[i].value = sel.options[i-1].value;
                   sel.options[i].selected = false;
                          
                   sel.options[i-1].text = selText;
                   sel.options[i-1].value = selValue;
                   sel.options[i-1].selected=true;
               }
            }
        }
     }
   	 
   	 function fnDown(lb) {   
       var sel = document.getElementById(lb);
       for(var i=sel.length - 2; i >= 0; i--) { 
           //???????????????????????????
           if(sel.options[i].selected){
              if(!sel.options.item(i+1).selected) {//?????????????
                   var selText = sel.options[i].text;
                   var selValue = sel.options[i].value;
                          
                   sel.options[i].text = sel.options[i+1].text;
                   sel.options[i].value = sel.options[i+1].value;
                   sel.options[i].selected = false;
                          
                   sel.options[i+1].text = selText;
                   sel.options[i+1].value = selValue;
                   sel.options[i+1].selected=true;
               }
            }
        }
     }
     
     function corpbindonsubmit()
     {
     	fnSelectall('lb_paramlist');
     	fnSelectall('lb_corpinfolist');
     	fnSelectall('lb_pointlist');
     }
     
     function deptbindonsubmit()
     {
     	fnSelectall('lb_deptinfomaplist');
     	fnSelectall('lb_deptinfomaplist1');
     }
     
     function staffbindonsubmit()
     {
     	fnSelectall('lb_userinfomaplist');
     	fnSelectall('lb_userinfomaplist1');
     }