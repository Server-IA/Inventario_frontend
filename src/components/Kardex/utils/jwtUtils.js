/**
 * @description Decodifica un JWT y extrae el payload
 * @param {string} jwt Token JWT completo (header.payload.signature)
 * @returns {object} Payload decodificado o {} si hay error
 */
export const decodeJwt = (jwt = "") => {
    try {
        const [, raw] = jwt.split(".");
        if (!raw) return {};
        const b64 = raw.replace(/-/g, "+").replace(/_/g, "/");
        const pad = b64.length % 4 === 2 ? "==" : b64.length % 4 === 3 ? "=" : "";
        const json = atob(b64 + pad);
        return JSON.parse(json);
    } catch {
        return {};
    }
};

/**
 * @description Extrae propiedades específicas del JWT
 * @param {string} jwt Token JWT
 * @returns {object} { rolId, empresaId, token }
 */
export const extractJwtData = (jwt = "") => {
    const decoded = decodeJwt(jwt);
    return {
        rolId: decoded?.rolId,
        empresaId: decoded?.empresaId,
        userId: decoded?.sub,
        token: jwt,
    };
};
