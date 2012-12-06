package com.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.util.EnumMap;
import java.util.Map;

public class MyActivity extends Activity {

    Map<UnitType, Integer> leftCounts = new EnumMap<UnitType, Integer>(UnitType.class);
    Map<UnitType, Integer> rightCounts = new EnumMap<UnitType, Integer>(UnitType.class);

    Map<UnitType, Integer> leftBonuses = new EnumMap<UnitType, Integer>(UnitType.class);
    Map<UnitType, Integer> rightBonuses = new EnumMap<UnitType, Integer>(UnitType.class);

    Map<UnitType, Integer> empty = new EnumMap<UnitType, Integer>(UnitType.class);

    boolean bonusesMode = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        clear(leftCounts);
        clear(rightCounts);
        clear(leftBonuses);
        clear(rightBonuses);
        clear(empty);
        setAllCounts();

        initButtons();

        Button run = (Button) findViewById(R.id.run);

        run.setOnClickListener(new View.OnClickListener() {
            public void onClick(View w) {

                Fleet leftFleet = new Fleet(leftCounts, empty, leftBonuses);
                Fleet rightFleet = new Fleet(rightCounts, empty, rightBonuses);

                SimulateOutput output = Simulator.instance.simulateSpaceBattle(leftFleet, rightFleet, 1000);

                TextView result = (TextView) findViewById(R.id.result);
                result.setText("left: " + output.attackerChance + " tied " + output.tieChance + " right: " + output.defenderChance);

            }
        });


        Button clearButton = (Button) findViewById(R.id.clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View w) {

                clear(leftBonuses);
                clear(rightBonuses);
                if (!bonusesMode) {
                    clear(leftCounts);
                    clear(rightCounts);
                }
                setAllCounts();
            }
        });


        Button bonusesButton = (Button) findViewById(R.id.bonuses);
        bonusesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View w) {
                bonusesMode = !bonusesMode;
                ((Button) w).setText(bonusesMode ? "Ships" : "Bonuses");
                setAllCounts();
            }
        });
    }

    private void decrease(Map<UnitType, Integer> units, UnitType unitType) {
        int value = units.get(unitType);
        if (value > 0 || bonusesMode)
            value--;
        units.put(unitType, value);

        setAllCounts();
    }

    private void increase(Map<UnitType, Integer> units, UnitType unitType) {
        int value = units.get(unitType);
        value++;
        units.put(unitType, value);

        setAllCounts();
    }

    private void clear(Map<UnitType, Integer> map) {
        for (UnitType type : UnitType.values()) {
            map.put(type, 0);
        }
    }

    private void setAllCounts() {
        setRowCounts(R.id.SL, R.id.SR, UnitType.WarSun);
        setRowCounts(R.id.DL, R.id.DR, UnitType.Dreadnought);
        setRowCounts(R.id.CL, R.id.CR, UnitType.Cruiser);
        setRowCounts(R.id.DsL, R.id.DsR, UnitType.Destroyer);
        setRowCounts(R.id.CaL, R.id.CaR, UnitType.Carrier);
        setRowCounts(R.id.FL, R.id.FR, UnitType.Fighter);
        setRowCounts(R.id.PL, R.id.PR, UnitType.Defense);
        setRowCounts(R.id.GL, R.id.GR, UnitType.GroundForce);
    }

    private void setRowCounts(int left, int right, UnitType type) {
        TextView l = (TextView) findViewById(left);
        TextView r = (TextView) findViewById(right);
        int leftValue = (bonusesMode ? leftBonuses : leftCounts).get(type);
        int rightValue = (bonusesMode ? rightBonuses : rightCounts).get(type);
        l.setText((leftValue > 0 && bonusesMode ? "+" : "") + Integer.toString(leftValue));
        r.setText((rightValue > 0 && bonusesMode ? "+" : "") + Integer.toString(rightValue));
    }

    private void initButtons() {
        initRow(R.id.SLM, R.id.SLP, R.id.SRM, R.id.SRP, UnitType.WarSun);
        initRow(R.id.DLM, R.id.DLP, R.id.DRM, R.id.DRP, UnitType.Dreadnought);
        initRow(R.id.CLM, R.id.CLP, R.id.CRM, R.id.CRP, UnitType.Cruiser);
        initRow(R.id.DsLM, R.id.DsLP, R.id.DsRM, R.id.DsRP, UnitType.Destroyer);
        initRow(R.id.CaLM, R.id.CaLP, R.id.CaRM, R.id.CaRP, UnitType.Carrier);
        initRow(R.id.FLM, R.id.FLP, R.id.FRM, R.id.FRP, UnitType.Fighter);
        initRow(R.id.PLM, R.id.PLP, R.id.PRM, R.id.PRP, UnitType.Defense);
        initRow(R.id.GLM, R.id.GLP, R.id.GRM, R.id.GRP, UnitType.GroundForce);
    }

    private void initRow(int leftMinus, int leftPlus, int rightMinus, int rightPlus, final UnitType type) {
        initTwoButtons(leftMinus, leftPlus, true, type);
        initTwoButtons(rightMinus, rightPlus, false, type);
    }

    private void initTwoButtons(int minus, int plus, final boolean useLeft, final UnitType type) {
        Button m = (Button) findViewById(minus);
        Button p = (Button) findViewById(plus);

        m.setOnClickListener(new View.OnClickListener() {
            public void onClick(View w) {
                Map<UnitType, Integer> map;
                if (useLeft)
                    map = (bonusesMode ? leftBonuses : leftCounts);
                else
                    map = (bonusesMode ? rightBonuses : rightCounts);
                decrease(map, type);
            }
        });

        p.setOnClickListener(new View.OnClickListener() {
            public void onClick(View w) {
                Map<UnitType, Integer> map;
                if (useLeft)
                    map = (bonusesMode ? leftBonuses : leftCounts);
                else
                    map = (bonusesMode ? rightBonuses : rightCounts);
                increase(map, type);
            }
        });
    }
}
