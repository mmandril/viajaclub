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
	 * Altera uma entidade já existente do banco de dados.
	 * @param entidade a ser alterada
	 */
	public void alterar(T entidade);

	/**
	 * Obtem o gerenciador de entidades, caso seja necessário a um repositório
	 * utilizar funções não existentes no repositório genérico.
	 * 
	 * @return EntityManager
	 */
	public EntityManager getEntityManager();

	/**
	 * Obtém uma instância da entidade, buscada do banco de dados a partir de
	 * sua chave primária
	 * @param type - classe mapeada no banco de dados
	 * @param id - valor da chave da tabela
	 * @return instância da entidade
	 */
	public T obterPorId(TipoId id);

	/**
	 * Obtém todas as entidades persistidas no banco de dados.
	 * @param type - classe mapeada no banco de dados
	 * @return
	 */
	public List<T> obterTodos();

	/**
	 * Limpa todas as instâncias de uma determinada classe do cache de segundo
	 * nível.
	 * @param type - classe mapeada no cache
	 */
	public void limparTodoCacheDe(Class<T> type);

	/**
	 * Limpa a instância de uma determinada classe do cache de segundo nível a
	 * partir de sua chave de identificação.
	 * @param type - classe mapeada no cache
	 * @param chace - chave da instância a ser excluída do cache.
	 */
	public void limparInstanciaDoCache(Class<T> type, Object chave);

	/**
	 * Obtém todas as entidades persistidas no banco de dados, e ao mesmo tempo, carrega os relacionamentos para evitar o problema de lazy load.
	 * @param relacionamentos - nomes dos atributos da classe dos quais desejam-se carregas os relacionamentos de modo EAGER.
	 * @return
	 */
	public List<T> obterTodosComRelacionamentos(String... relacionamentos);

	/**
	 * Obtém uma instância da entidade, buscada do banco de dados a partir de
	 * sua chave primária, e ao mesmo tempo, carrega os relacionamentos para evitar o problema de lazy load.
	 * 
	 * @param id - valor da chave da tabela
	 * @param relacionamentos - nomes dos atributos da classe dos quais desejam-se carregas os relacionamentos de modo EAGER.
	 * @return instância da entidade 
	 */
	public T obterPorIdComRelacionamentos(TipoId id, String... relacionamentos);

	/**
	 * Obtém instâncias da entidade, buscada do banco de dados a partir de parâmetros desejados,
	 *  e ao mesmo tempo, carrega os relacionamentos para evitar o problema de lazy load.
	 * 
	 * @param parametros - lista de parametros para busca na forma de chave(nome do atributo)-valor 
	 * @param relacionamentos - nomes dos atributos da classe dos quais desejam-se carregas os relacionamentos de modo EAGER.
	 * @return instância da entidade 
	 */
	public List<T> obterComRelacionamentosPor(Map<String, Object> parametros, String... relacionamentos);

}
