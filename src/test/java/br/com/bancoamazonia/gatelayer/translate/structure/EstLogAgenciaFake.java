package br.com.bancoamazonia.gatelayer.translate.structure;

import br.com.bancoamazonia.gatelayer.translate.AgencyCode;
import br.com.bancoamazonia.gatelayer.translate.PositionalField;

public class EstLogAgenciaFake {

    @PositionalField(length = 5, description = "Sem descrição")
    private String campo1;

    @PositionalField(length = 5, description = "Sem descrição")
    private String campo2;

    @AgencyCode
    @PositionalField(length = 5, description = "Código da agência")
    private String codigoAgencia;
}
