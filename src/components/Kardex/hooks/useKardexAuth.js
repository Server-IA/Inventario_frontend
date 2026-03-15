import { useMemo } from "react";
import { extractJwtData } from "../utils/jwtUtils";
import { ROLES } from "../constants/kardexConstants";

/**
 * @description Hook para obtener información de autenticación desde JWT
 * @returns {object} { rolId, empresaId, isAdmin, token }
 */
export const useKardexAuth = () => {
    const token = localStorage.getItem("token");

    const authData = useMemo(() => {
        const jwtData = extractJwtData(token);
        return {
            rolId: jwtData.rolId,
            empresaId: jwtData.empresaId,
            userId: jwtData.userId,
            token: jwtData.token,
            isAdmin: jwtData.rolId === ROLES.ADMINISTRADOR_SISTEMA,
            isEmpresaAdmin: jwtData.rolId === ROLES.ADMINISTRADOR_EMPRESA,
        };
    }, [token]);

    return authData;
};
