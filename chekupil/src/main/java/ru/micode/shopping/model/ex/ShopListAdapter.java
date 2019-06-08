package ru.micode.shopping.model.ex;

import org.apache.commons.lang3.StringUtils;
import ru.micode.shopping.model.Buy;
import ru.micode.shopping.model.Shopping;
import ru.micode.shopping.service.GroupService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Petr Gusarov on 03.11.17.
 */
public class ShopListAdapter {

    public static Shopping convert(ExShopList exShopList) {
        Shopping sl = new Shopping();
        sl.setNew(true);
        sl.setName(exShopList.getName());
        sl.setComment(exShopList.getDescription());
        return sl;
    }

    /**
     * Конвертирует список во внешнюю модель
     *
     * @param shopping      родительский список
     * @param buys          покупки
     * @param senderUuid    код отправителя
     * @param recipientUuid код получателя
     * @return
     */
    public static ExShopList convert(Shopping shopping, List<Buy> buys, String senderUuid, String recipientUuid) {
        final ExShopList exShopList = new ExShopList();
        exShopList.setName(shopping.getName());
        exShopList.setDescription("Купи это...");
        exShopList.setRecipient(new ExRecipient(senderUuid, recipientUuid));
        List<ExBuy> buyList = exShopList.getBuyList();
        for (Buy buy : buys) {
            if (!buy.isDone()) {
                ExBuy exBuy = new ExBuy(
                    buy.getName(),
                    buy.getAmount(),
                    buy.getMeasure(),
                    buy.getPositionGroup(),
                    buy.getNameGroup()
                );
                buyList.add(exBuy);
            }
        }
        return exShopList;
    }

    public static List<Buy> convert(List<ExBuy> exBuyList, Shopping parentList) {
        List<Buy> buys = new ArrayList<>();
        for (ExBuy exBuy : exBuyList) {
            buys.add(convert(exBuy, parentList));
        }
        return buys;
    }

    public static Buy convert(ExBuy exBuy, Shopping parentList) {
        Buy buy = new Buy();
        buy.setName(exBuy.getName());
        buy.setAmount(exBuy.getAmount() == null ? 0 : exBuy.getAmount());
        buy.setMeasure(StringUtils.isBlank(exBuy.getMeasure()) ? null : exBuy.getMeasure());
        buy.setPositionGroup(exBuy.getPosition() == null ? 0 : exBuy.getPosition());
        buy.setNameGroup(exBuy.getNameGroup() == null ? GroupService.UNDEFINED_GROUP.getName() : exBuy.getNameGroup());
        buy.setShopping(parentList);
        return buy;
    }
}
