package br.com.viajaclub.model.dao;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

public interface GenericDao<T, TipoId> {
	
	/**
	 * Persiste uma entidade no banco de dados.
	 * @param entidade a ser persistida
	 */
	public void salvar(T entidade);

	/**
	 * Exclui uma entidade do banco de dados.
	 * @param entidade a ser excluida
	 */
	public void excluir(T entidade);

	/**
	 * Altera uma entidade j� existente do banco de dados.
	 * @param entidade a ser alterada
	 */
	public void alterar(T entidade);

	/**
	 * Obtem o gerenciador de entidades, caso seja necess�rio a um reposit�rio
	 * utilizar fun��es n�o existentes no reposit�rio gen�rico.
	 * 
	 * @return EntityManager
	 */
	public EntityManager getEntityManager();

	/**
	 * Obt�m uma inst�ncia da entidade, buscada do banco de dados a partir de
	 * sua chave prim�ria
	 * @param type - classe mapeada no banco de dados
	 * @param id - valor da chave da tabela
	 * @return inst�ncia da entidade
	 */
	public T obterPorId(TipoId id);

	/**
	 * Obt�m todas as entidades persistidas no banco de dados.
	 * @param type - classe mapeada no banco de dados
	 * @return
	 */
	public List<T> obterTodos();

	/**
	 * Limpa todas as inst�ncias de uma determinada classe do cache de segundo
	 * n�vel.
	 * @param type - classe mapeada no cache
	 */
	public void limparTodoCacheDe(Class<T> type);

	/**
	 * Limpa a inst�ncia de uma determinada classe do cache de segundo n�vel a
	 * partir de sua chave de identifica��o.
	 * @param type - classe mapeada no cache
	 * @param chace - chave da inst�ncia a ser exclu�da do cache.
	 */
	public void limparInstanciaDoCache(Class<T> type, Object chave);

	/**
	 * Obt�m todas as entidades persistidas no banco de dados, e ao mesmo tempo, carrega os relacionamentos para evitar o problema de lazy load.
	 * @param relacionamentos - nomes dos atributos da classe dos quais desejam-se carregas os relacionamentos de modo EAGER.
	 * @return
	 */
	public List<T> obterTodosComRelacionamentos(String... relacionamentos);

	/**
	 * Obt�m uma inst�ncia da entidade, buscada do banco de dados a partir de
	 * sua chave prim�ria, e ao mesmo tempo, carrega os relacionamentos para evitar o problema de lazy load.
	 * 
	 * @param id - valor da chave da tabela
	 * @param relacionamentos - nomes dos atributos da classe dos quais desejam-se carregas os relacionamentos de modo EAGER.
	 * @return inst�ncia da entidade 
	 */
	public T obterPorIdComRelacionamentos(TipoId id, String... relacionamentos);

	/**
	 * Obt�m inst�ncias da entidade, buscada do banco de dados a partir de par�metros desejados,
	 *  e ao mesmo tempo, carrega os relacionamentos para evitar o problema de lazy load.
	 * 
	 * @param parametros - lista de parametros para busca na forma de chave(nome do atributo)-valor 
	 * @param relacionamentos - nomes dos atributos da classe dos quais desejam-se carregas os relacionamentos de modo EAGER.
	 * @return inst�ncia da entidade 
	 */
	public List<T> obterComRelacionamentosPor(Map<String, Object> parametros, String... relacionamentos);

}
