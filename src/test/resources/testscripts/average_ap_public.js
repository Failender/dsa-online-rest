var total = 0;
total -=params.torfMissingAp
for each (var held in params.helden) {

    total += held.angaben.ap.gesamt;

}
log('testlog')
total /= params.helden.length;
return total;