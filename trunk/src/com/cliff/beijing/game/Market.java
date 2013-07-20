package com.cliff.beijing.game;

public class Market {

	protected final int base[] = new int[]{100, 15000, 5, 1000, 5000, 250, 750, 65}; 
	protected final int rand[] = new int[]{350, 15000, 50, 2500, 9000, 600, 750, 180}; 

	public int[] getGoodPrices(int leave) {
		int prices[] = new int[Constants.GOOD_TYPE_COUNT];
		for (int i = 0; i < prices.length; i++)
			prices[i] = base[i] + Constants.getRandom(rand[i]);
		
		for (int i = 0; i < leave; i++)
			prices[Constants.getRandom(Constants.GOOD_TYPE_COUNT)] = 0;

		return prices;
	}

//////// Original Code
//	void CSelectionDlg::makeDrugPrices(int leaveout)
//	{
//	    int i, j;
//
//		m_DrugPrice [0] = 100 + RandomNum(350);
//		m_DrugPrice [1] = 15000 + RandomNum(15000);
//		m_DrugPrice [2] = 5 + RandomNum(50);
//		m_DrugPrice [3] = 1000 + RandomNum(2500);
//		m_DrugPrice [4] = 5000 + RandomNum(9000);
//		m_DrugPrice [5] = 250 + RandomNum(600);
//		m_DrugPrice [6] = 750 + RandomNum(750);
//		m_DrugPrice [7] = 65 + RandomNum(180);
//
//		for (i = 0; i < leaveout; i++)
//		{
//			j = RandomNum(8);
//			m_DrugPrice [j] = 0;
//		}
//	}
//	strcpy(m_chDrugName[0],"进口香烟");	
//	strcpy(m_chDrugName[1],"走私汽车");
//	strcpy(m_chDrugName[2],"盗版VCD、游戏");	
//	strcpy(m_chDrugName[3],"假白酒（剧毒！）");
//	strcpy(m_chDrugName[4],"《上海小宝贝》（禁书）");	
//	strcpy(m_chDrugName[5],"进口玩具");
//	strcpy(m_chDrugName[7],"伪劣化妆品");	
//	strcpy(m_chDrugName[6],"水货手机");

	
}
