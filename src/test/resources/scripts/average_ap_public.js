var total = 0;
for each (var held in params.helden) {

    total += held.angaben.ap.gesamt;
}
total /= params.helden.length;
return total;