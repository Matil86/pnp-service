export class RaceAttributes{
  STR: number;
  DEX: number;
  CON: number;
  INT: number;
  WIS: number;
  CHA: number;
  DEX_MAX:number;
  STR_MAX: number;
  CON_MAX: number;
  INT_MAX: number;
  WIS_MAX: number;
  CHA_MAX: number;


  constructor(STR: number, DEX: number, CON: number, INT: number, WIS: number, CHA: number, DEX_MAX: number, STR_MAX: number, CON_MAX: number, INT_MAX: number, WIS_MAX: number, CHA_MAX: number) {
    this.STR = STR;
    this.DEX = DEX;
    this.CON = CON;
    this.INT = INT;
    this.WIS = WIS;
    this.CHA = CHA;
    this.DEX_MAX = DEX_MAX;
    this.STR_MAX = STR_MAX;
    this.CON_MAX = CON_MAX;
    this.INT_MAX = INT_MAX;
    this.WIS_MAX = WIS_MAX;
    this.CHA_MAX = CHA_MAX;
  }
}
