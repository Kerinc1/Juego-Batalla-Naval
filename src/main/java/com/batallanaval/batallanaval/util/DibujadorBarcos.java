package com.batallanaval.batallanaval.util;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import com.batallanaval.batallanaval.model.Barco;
import com.batallanaval.batallanaval.model.Orientacion;

/**
 * Dibuja barcos utilizando figuras 2D de JavaFX.
 */
public class DibujadorBarcos {
    private static final double TAMAÑO_BASE = 42.0;
    private static final Color COLOR_BARCO = Color.web("#7F8790");
    private static final Color COLOR_BORDE = Color.web("#4A4E55");
    private static final Color COLOR_ESCOTILLA = Color.web("#2F3135");
    private static final double GROSOR_BORDE = 1.6;

    /**
     * Crea un grupo visual que representa el barco.
     */
    public static Group crearVisualizacionBarco(Barco barco) {
        Group grupo = new Group();
        int tamaño = barco.getTipo().getTamaño();
        Orientacion orientacion = barco.getOrientacion();

        double cuerpoAncho = orientacion == Orientacion.HORIZONTAL ? Math.max(28.0, tamaño * TAMAÑO_BASE - 8) : 28.0;
        double cuerpoAlto = orientacion == Orientacion.HORIZONTAL ? 24.0 : Math.max(28.0, tamaño * TAMAÑO_BASE - 8);

        Rectangle cuerpo = new Rectangle(4, 7, cuerpoAncho, cuerpoAlto);
        cuerpo.setArcWidth(12);
        cuerpo.setArcHeight(12);
        cuerpo.setFill(COLOR_BARCO);
        cuerpo.setStroke(COLOR_BORDE);
        cuerpo.setStrokeWidth(GROSOR_BORDE);
        grupo.getChildren().add(cuerpo);

        Polygon proa = new Polygon();
        if (orientacion == Orientacion.HORIZONTAL) {
            proa.getPoints().addAll(4.0, 7.0, 4.0, 31.0, 0.0, 19.0);
        } else {
            proa.getPoints().addAll(4.0, 7.0, 28.0, 7.0, 16.0, 3.0);
        }
        proa.setFill(COLOR_BARCO);
        proa.setStroke(COLOR_BORDE);
        proa.setStrokeWidth(GROSOR_BORDE);
        grupo.getChildren().add(proa);

        Rectangle popa = new Rectangle(cuerpoAncho + 2, 8, 10, cuerpoAlto - 2);
        popa.setArcWidth(10);
        popa.setArcHeight(10);
        popa.setFill(COLOR_BARCO);
        popa.setStroke(COLOR_BORDE);
        popa.setStrokeWidth(GROSOR_BORDE);
        grupo.getChildren().add(popa);

        Rectangle cubierta = new Rectangle(10, 12, Math.max(18.0, cuerpoAncho - 12), Math.max(10.0, cuerpoAlto - 8));
        cubierta.setFill(Color.web("#8D949D"));
        cubierta.setStroke(COLOR_BORDE);
        cubierta.setStrokeWidth(1.2);
        grupo.getChildren().add(cubierta);

        Line linea = new Line(10, 10, cuerpoAncho + 3, 10);
        linea.setStroke(COLOR_BORDE);
        linea.setStrokeWidth(1.0);
        grupo.getChildren().add(linea);

        Path quilla = new Path();
        quilla.getElements().addAll(
            new javafx.scene.shape.MoveTo(12, 31),
            new javafx.scene.shape.LineTo(cuerpoAncho + 2, 31),
            new javafx.scene.shape.LineTo(cuerpoAncho + 8, 34),
            new javafx.scene.shape.LineTo(12, 34),
            new javafx.scene.shape.ClosePath()
        );
        quilla.setFill(Color.web("#6B7179"));
        quilla.setStroke(COLOR_BORDE);
        quilla.setStrokeWidth(0.9);
        grupo.getChildren().add(quilla);

        for (int i = 0; i < Math.min(tamaño, 3); i++) {
            double cx = orientacion == Orientacion.HORIZONTAL ? 18 + i * 14 : 18;
            double cy = orientacion == Orientacion.VERTICAL ? 16 + i * 14 : 18;
            Circle escotilla = new Circle(cx, cy, 3.2);
            escotilla.setFill(COLOR_ESCOTILLA);
            grupo.getChildren().add(escotilla);
        }

        return grupo;
    }
}
